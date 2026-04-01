package com.techlab.store.service;

import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderDetail;
import com.techlab.store.entity.Product;
import com.techlab.store.repository.ClientRepository;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.repository.ProductRepository;

import com.techlab.store.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderFullDTO createOrder(OrderFullDTO dto, Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Set<OrderDetail> failed = new HashSet<>();

        Order newOrder = orderMapper.toEntity(dto);
        newOrder.setClient(client);
        newOrder.setState(Order.OrderState.SIN_PAGAR);
        newOrder.setTotalAmount(dto.getTotalAmount());
        newOrder.setCreatedAt(java.time.LocalDateTime.now());

        for (OrderDetail detail : newOrder.getDetails()) {
            detail.setOrder(newOrder);  // establece relacion order<->orderDetail
            if(inventoryService.decreaseStock(
                    detail.getProduct().getId(),
                    detail.getQuantity()
            )) failed.add(detail) ;
        }
        if(failed.isEmpty()){
            newOrder.setFailedDetails(failed);
        }
        Order savedOrder = orderRepository.save(newOrder);
        return orderMapper.toFullDto(savedOrder);
    }






    @Transactional(readOnly = true)
    public OrderFullDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return this.orderMapper.toFullDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderFullDTO> getAll() {
        List<Order> orders = orderRepository.findAll();
        return this.orderMapper.toFullDtoList(orders);
    }


    @Transactional
    public OrderFullDTO updateStatus(Long id, Order.OrderState newState) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        log.info("Actualizando estado de Pedido ID {} de {} a {}", id, order.getState(), newState);

        if (newState == Order.OrderState.CANCELADO) {
            log.warn("Pedido cancelado: Reponiendo stock.");
            for(OrderDetail detail : order.getDetails()){
                Product p = detail.getProduct();
                p.setStock(p.getStock() + detail.getQuantity());
            }
        }
        order.setState(newState);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toFullDto(savedOrder);
    }

    public OrderFullDTO updateDetail(Long id, Set<OrderDetail> details){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        order.setDetails(details);

        Order savedOrder = this.orderRepository.save(order);
        return this.orderMapper.toFullDto(savedOrder);
    }



    @Transactional
    public OrderFullDTO updateById(Long id, Order dataToEdit) {
        Order existingOrder = orderRepository.findOneWithDetailsAndClientById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        validateOrderStateForEdit(existingOrder);

        if (dataToEdit.getClient() != null) {
            existingOrder.setClient(dataToEdit.getClient());
        }

        existingOrder.setState(dataToEdit.getState());
        if (dataToEdit.getDetails() != null) {
            this.updateOrderDetailsAndStock(existingOrder, dataToEdit.getDetails());
        }

        Order saveOrder = this.orderRepository.save(existingOrder);
        return this.orderMapper.toFullDto(saveOrder);
    }





    private void updateOrderDetailsAndStock(Order existingOrder, Set<OrderDetail> newDetails) {

        Map<Long, OrderDetail> oldDetailsMap = existingOrder.getDetails().stream()
                .collect(Collectors.toMap(
                        detail -> detail.getProduct().getId(),
                        detail -> detail
                ));

        for (OrderDetail newDetail : newDetails) {
            Long productId = newDetail.getProduct().getId();
            OrderDetail oldDetail = oldDetailsMap.get(productId);
            // modificamos el stock de los productos
            this.updateStockForModifiedDetail(newDetail, oldDetail);
            newDetail.setOrder(existingOrder);
            // voy vaciando el oldDetail para que solo queden los
            // productos eliminados del pedido
            oldDetailsMap.remove(productId);
        }
        // restaura el stock del los productos eliminados del pedido.
        this.restoreStockForDeletedDetails(oldDetailsMap);
        existingOrder.getDetails().clear();
        existingOrder.getDetails().addAll(newDetails);
    }


    private void validateOrderStateForEdit(Order order) {
        if (order.getState() == Order.OrderState.COMPLETO ||
                order.getState() == Order.OrderState.EN_ENVIO) {
            throw new RuntimeException("No se pueden editar los detalles de una orden en estado COMPLETO o EN_ENVIO.");
        }
    }


    private void updateStockForModifiedDetail(OrderDetail newDetail, OrderDetail oldDetail) {

        Long productId = newDetail.getProduct().getId();
        int newQuantity = newDetail.getQuantity();
        int oldQuantity = (oldDetail != null) ? oldDetail.getQuantity() : 0;

        int stockAdjustment = oldQuantity - newQuantity;

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto con ID " + productId + " no encontrado."));

        if (product.getStock() + stockAdjustment < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
        }

        product.setStock(product.getStock() + stockAdjustment);
        productRepository.save(product);
    }


    private void restoreStockForDeletedDetails(Map<Long, OrderDetail> deletedDetailsMap) {
        for (OrderDetail deletedDetail : deletedDetailsMap.values()) {
            Product product = productRepository.findById(deletedDetail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

            // restaura el stock completo del ítem eliminado
            product.setStock(product.getStock() + deletedDetail.getQuantity());
            productRepository.save(product);
        }
    }

    public void restoreStockForCanceledOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrado."));

        Set<OrderDetail> details = order.getDetails();

        for (OrderDetail deletedDetail : details) {
            Product product = productRepository.findById(deletedDetail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado."));

            // restaura el stock completo del ítem eliminado
            product.setStock(product.getStock() + deletedDetail.getQuantity());
            productRepository.save(product);
        }
    }


    public List<OrderFullDTO> getOrderByClientId(Long id) {
//      List<Order> orders = this.orderRepository.findAllWithDetailsAndClientById(id);
        List<Order> orders = this.orderRepository.findByClientId(id);
        return this.orderMapper.toFullDtoList(orders);
    }

    public List<OrderFullDTO> getByUser(String username) {
        List<Order> orders  = this.orderRepository.findAllByFirstName(username);
        return this.orderMapper.toFullDtoList(orders);
    }


    @Scheduled(fixedRate = 300000) // cada 5 minutos
public void cleanupPendingOrders() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
    List<Order> oldOrders = orderRepository.findByStateAndCreatedAtBefore("SIN_PAGAR", cutoff);
    
    for (Order order : oldOrders) {
        order.setState(Order.OrderState.CANCELADO);
        restoreStockForCanceledOrder(order.getId());
        orderRepository.save(order);
    }
}
}
