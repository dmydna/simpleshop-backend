package com.techlab.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.OrderComplete;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.mapper.OrderMapper;
import com.techlab.store.repository.ClientRepository;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.specification.OrderSpecifications;
import com.techlab.store.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    // TODO mover logica DTO a Controller y dejar solo entities.
    // TODO elimnar metodos no utilizados.

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final ListingRepository listingRepository;

    @Autowired
    private final OrderMapper orderMapper;

    @Transactional
    public OrderComplete createOrder(OrderComplete dto, Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<OrderItem> failed = new ArrayList<>();

        Order newOrder = orderMapper.toEntity(dto);
        newOrder.setClient(client);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setTotalAmount(dto.totalAmount());
        newOrder.setCreatedAt(java.time.LocalDateTime.now());

        for (OrderItem detail : newOrder.getItems()) {
            detail.setOrder(newOrder);  // establece relacion order<->orderDetail
            if (inventoryService.decreaseStock(
                    detail.getListing().getId(),
                    detail.getQuantity()
            )) {
                failed.add(detail);
            }
        }
        if (failed.isEmpty()) {
            newOrder.setFailedItems(failed);
        }
        Order savedOrder = orderRepository.save(newOrder);
        return orderMapper.toFullDto(savedOrder);
    }

    public Page<OrderComplete> filter(
            Long userId,
            OrderStatus status,
            Pageable pageable
    ) {
        
    Specification<Order> spec = Specification
        .where(OrderSpecifications.hasClientId(userId))
        .and(OrderSpecifications.hasStatus(status));

        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        return ordersPage.map(order -> this.orderMapper.toFullDto(order));
    }



    @Transactional(readOnly = true)
    public Order getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderComplete> getAll() {
        List<Order> orders = orderRepository.findAll();
        return this.orderMapper.toFullDtoList(orders);
    }

    @Transactional
    public OrderComplete updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        log.info("Actualizando estado de Pedido ID {} de {} a {}", id, order.getStatus(), newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            log.warn("Pedido cancelado: Reponiendo stock.");
            for (OrderItem detail : order.getItems()) {
                Listing l = listingRepository.findById(detail.getListing().getId() )
                    .orElseThrow(() -> new RuntimeException("Listing no encontrado."));
                l.setStock(l.getStock() + detail.getQuantity());
            }
        }
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toFullDto(savedOrder);
    }

    public OrderComplete updateDetail(Long id, List<OrderItem> items) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        order.setItems(items);

        Order savedOrder = this.orderRepository.save(order);
        return this.orderMapper.toFullDto(savedOrder);
    }

    @Transactional
    public OrderComplete updateById(Long id, Order dataToEdit) {
        Order existingOrder = orderRepository.findOneWithDetailsAndClientById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        validateOrderStatusForEdit(existingOrder);

        if (dataToEdit.getClient() != null) {
            existingOrder.setClient(dataToEdit.getClient());
        }

        existingOrder.setStatus(dataToEdit.getStatus());
        if (dataToEdit.getItems() != null) {
            this.updateOrderItemsAndStock(existingOrder, dataToEdit.getItems());
        }

        Order saveOrder = this.orderRepository.save(existingOrder);
        return this.orderMapper.toFullDto(saveOrder);
    }

    private void updateOrderItemsAndStock(Order existingOrder, List<OrderItem> newDetails) {

        Map<Long, OrderItem> oldDetailsMap = existingOrder.getItems().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getListing().getId(),
                        detail -> detail
                ));

        for (OrderItem newDetail : newDetails) {
            Long listingId = newDetail.getListing().getId();
            OrderItem oldDetail = oldDetailsMap.get(listingId);
            // modificamos el stock de los productos
            this.updateStockForModifiedDetail(newDetail, oldDetail);
            newDetail.setOrder(existingOrder);
            // voy vaciando el oldDetail para que solo queden los
            // productos eliminados del pedido
            oldDetailsMap.remove(listingId);
        }
        // restaura el stock del los productos eliminados del pedido.
        this.restoreStockForDeletedDetails(oldDetailsMap);
        existingOrder.getItems().clear();
        existingOrder.getItems().addAll(newDetails);
    }

    private void validateOrderStatusForEdit(Order order) {
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("No se pueden editar los detalles de una orden en estado COMPLETO o EN_ENVIO.");
        }
    }

    private void updateStockForModifiedDetail(OrderItem newDetail, OrderItem oldDetail) {

        int newQuantity = newDetail.getQuantity();
        int oldQuantity = (oldDetail != null) ? oldDetail.getQuantity() : 0;

        int stockAdjustment = oldQuantity - newQuantity;

        Listing listing = listingRepository.findById(oldDetail.getListing().getId())
                    .orElseThrow(() -> new RuntimeException("Listing no encontrado."));

        if (listing.getStock() + stockAdjustment < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + 
            listing.getProduct().getName());
        }

        listing.setStock(listing.getStock() + stockAdjustment);
        listingRepository.save(listing);
    }

    private void restoreStockForDeletedDetails(Map<Long, OrderItem> deletedDetailsMap) {
        for (OrderItem deletedDetail : deletedDetailsMap.values()) {
            Listing listing = listingRepository.findById(deletedDetail.getListing().getId() )
                    .orElseThrow(() -> new RuntimeException("Listing no encontrado."));
            // restaura el stock completo del ítem eliminado
            listing.setStock(listing.getStock() + deletedDetail.getQuantity());
            listingRepository.save(listing);
        }
    }

    public void deleteOrderAndRestoreStock(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrado."));

        List<OrderItem> items = order.getItems();

        for (OrderItem deletedDetail : items) {
            Listing listing = listingRepository.findById(deletedDetail.getListing().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

            // restaura el stock completo del ítem eliminado
            listing.setStock(listing.getStock() + deletedDetail.getQuantity());
            listingRepository.save(listing);
        }
        orderRepository.delete(order);
    }


    public boolean cancelOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden de comprar no encontrado."));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            return false;
        }
        deleteOrderAndRestoreStock(order.getId());
        return true;
    }

}
