package com.techlab.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.repository.OrderRepository;
import com.techlab.store.specification.OrderSpecifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    // CHECKME: mueve logica DTO a Controller y dejar solo entities.
    // TODO elimnar metodos legacy  o no utilizados.
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ListingService listingService;
    private final PriceService priceService;

    // NOTA: 
    // 1. El pedido nunca se actualiza solo se reemplaza por uno nuevo.
    // 2. El pedido con estado "PENDING" es resuelto en el momento o es eliminado
    // de forma fisica en el proximo ciclo de compra.
    // 3. Se puede asumir que no hay mas de 1 pedido con estado "PENDING" por usuario.
    @Transactional
    public Order createOrder(Order order) {
        // Borra Pedidos Pendientes del usuario 
        // (que no fueron resueltos en el ciclo de compra anterior)
        userCleanupExpiredPendingOrders(order.getClient().getId());
        // Borra pedidos pendientes Globales (solo los que pasaron 1 hora desde su emision)
        // Nota: esto libera stock reservado si los usuarios no cancelaron el pedido 
        globalCleanupExpiredPendingOrders();
        Order processedOrder = processOrder(order);
        return orderRepository.save(processedOrder);
    }


    public Order processOrder(Order order){
        List<OrderItem> failed = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Listing listing = null;

        for (OrderItem detail : order.getItems()) {
            listing = detail.getListing();
            log.info("order detail { id: {}, quantity: {}, getPriceAtPurchase: {} }", 
                detail.getId(), detail.getQuantity(), detail.getPriceAtPurchase());
            // Validacion de LISTING 
            if(!listing.getStatus().equals(ListingStatus.ACTIVE)){
                log.warn("[FALLO VALIDACIÓN] El listing no está ACTIVE. Estado actual: {}", listing.getStatus());
                failed.add(detail);
                continue;
            }
            // Validacion de PRECIO
            if(!priceService.validateItemPrice(detail, listing)){
                log.warn("[FALLO VALIDACIÓN] El precio no coincide. Frontend: {}, Backend calculó: {}",
                detail.getPriceAtPurchase(), priceService.getFinalPrice(listing)    
            ); 
                failed.add(detail);
                continue;
            }
            // Validacion de STOCK
            if (!inventoryService.decreaseStock(
                listing.getId(),
                detail.getQuantity())
            ) {
                log.warn("[FALLO VALIDACIÓN] No hay suficiente stock para el listing ID: {}", listing.getId());
                failed.add(detail);
                continue;
            }
            // calculamos manualmente el totalAmount
            // Nota: recordar que totalAmount viene del frontend.
            if (detail.getPriceAtPurchase() != null) {
                log.info("INCREMENTA TOTAL_AMOUNT");
                BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
                log.info("QUANTITY: {}", quantity);
                BigDecimal itemSubtotal = detail.getPriceAtPurchase().multiply(quantity);
                totalAmount = totalAmount.add(itemSubtotal); // Reasigna siempre el resultado
            }
            
        }

        // Validacion de TOTAL_AMOUNT
        if (order.getTotalAmount() == null || totalAmount.compareTo(order.getTotalAmount()) != 0) {
            log.info("Comparando: " + totalAmount.toPlainString() + " vs " + order.getTotalAmount().toPlainString());
            throw new RuntimeException("Error al crear Orden, detalles: failed totalAmount"); 
        }

        // Validacion de Items fallidos 
        if (!failed.isEmpty()) {

            // Borra los items fallidos de la orden original.
            Set<Long> failedIds = failed.stream()
                .map(oi -> oi.getListing().getId())
                .collect(Collectors.toSet());

            order.getItems().removeIf(item -> 
                failedIds.contains(item.getListing().getId())); 

            order.setFailedItems(failed);
        }

        return order;
    }


    // NOTA:
    // 1. Setea como inactiva Publicacion si se queda sin stock 
    // (al momento de comprar)
    // 2. Setea como activa Publicacion (al momento de cancelar la compra)
    public void updateStatusListingForStock(Listing listing){
        ListingStatus currentStatus = listing.getStatus();
        if( listing.getStock() == 0 && 
            currentStatus.equals(ListingStatus.ACTIVE)){
            listingService.updateStatusById(
                listing.getId(), ListingStatus.INACTIVE
            );
        };
        if(listing.getStock() != 0 && 
            currentStatus.equals(ListingStatus.INACTIVE)){
            listingService.updateStatusById(
                listing.getId(), ListingStatus.ACTIVE
            );
        }
    }

    public void userCleanupExpiredPendingOrders(Long userId){
        // Eliminamos ordenes inpagas y restauramos stocks;
        List<Order> listOrders = orderRepository.findByClientIdAndStatus(userId, OrderStatus.PENDING);
        for(Order order : listOrders){
             // Eliminacion permanente. (1 hora despues de su emision)
            LocalDateTime now = LocalDateTime.now();
            if(order.getCreatedAt().isAfter(now.plusHours(1)))
              inventoryService.deleteOrderAndRestoreStock(order.getId());
        }
    }


    public void globalCleanupExpiredPendingOrders(){
        // Eliminamos ordenes inpagas y restauramos stocks;
        List<Order> listOrders = orderRepository.findAllByStatus(OrderStatus.PENDING);
        for(Order order : listOrders){
             // Eliminacion permanente. (24 horas despues de su emision)
            LocalDateTime now = LocalDateTime.now();
            if(order.getCreatedAt().equals(now.plusHours(24)))
              inventoryService.deleteOrderAndRestoreStock(order.getId());
        }
    }




    public Page<Order> filter(
            Long userId,
            OrderStatus status,
            Pageable pageable
    ) {
        
    Specification<Order> spec = Specification.allOf(
            OrderSpecifications.hasClientId(userId),
            OrderSpecifications.hasStatus(status)
        );

        return orderRepository.findAll(spec, pageable);
    }



    @Transactional(readOnly = true)
    public Order getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return order;
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = getById(id);

        log.info("🔔 Actualizando status de Pedido ID {} de {} a {}", id, order.getStatus(), newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            log.warn("Pedido cancelado: Reponiendo stock.");
            for (OrderItem detail : order.getItems()) {
                Listing l = listingService.getById(detail.getListing().getId() );
                l.setStock(l.getStock() + detail.getQuantity());
            }
        }
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    // TODO: eliminar. Metodo deprecado (sin uso)
    public Order updateDetail(Long id, List<OrderItem> items) {
        Order order = getById(id);
        order.setItems(items);
        return orderRepository.save(order);
    }


    // @legacy
/*    @Transactional
    public Order updateById(Long id, Order dataToEdit) {
        Order existingOrder = orderRepository.findOneWithDetailsAndClientById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        validateOrderStatusForEdit(existingOrder);

        orderMapper.updateFromEntity(dataToEdit, existingOrder);

        existingOrder.setStatus(dataToEdit.getStatus());
        existingOrder.setUpdatedAt(LocalDateTime.now());
        if (dataToEdit.getItems() != null) {
            this.updateOrderItemsAndStock(existingOrder, dataToEdit.getItems());
        }
 
        return orderRepository.save(existingOrder);
    }*/


    // @legacy
/*    private void updateOrderItemsAndStock(Order existingOrder, List<OrderItem> newDetails) {

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
    }*/

    // @legacy
/*    @Transactional
    private void updateStockForModifiedDetail(OrderItem newDetail, OrderItem oldDetail) {

        int newQuantity = newDetail.getQuantity();
        int oldQuantity = (oldDetail != null) ? oldDetail.getQuantity() : 0;

        int stockAdjustment = oldQuantity - newQuantity;

        Listing listing = listingService.getById(oldDetail.getListing().getId());

        if (listing.getStock() + stockAdjustment < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + 
            listing.getProduct().getName());
        }

        listing.setStock(listing.getStock() + stockAdjustment);
        // listingRepository.save(listing);
    }*/

    //@legacy
/*    @Transactional
    private void restoreStockForDeletedDetails(Map<Long, OrderItem> deletedDetailsMap) {
        for (OrderItem deletedDetail : deletedDetailsMap.values()) {
            Listing listing = listingService.getById(deletedDetail.getListing().getId());
            // restaura el stock completo del ítem eliminado
            listing.setStock(listing.getStock() + deletedDetail.getQuantity());
           // listingRepository.save(listing);
        }
    }*/

/*    @Transactional
    public void deleteOrderAndRestoreStock(Long orderId) {
        Order order = getById(orderId);

        List<OrderItem> items = order.getItems();

        for (OrderItem deletedDetail : items) {
            Listing listing = listingService.getById(deletedDetail.getListing().getId());

            // restaura el stock completo del ítem eliminado
            listing.setStock(listing.getStock() + deletedDetail.getQuantity());
            // listingRepository.save(listing);
        }
        orderRepository.delete(order);
    }*/



    public Order getByHash(String hash, Long userId){
        Order order = this.orderRepository.findByUserIdAandHash(userId, hash)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado con hash: " + hash));
        
        return order;
    }


    public boolean cancelOrderById(Long orderId) {
        Order order = getById(orderId);

        if (!order.getStatus().equals(OrderStatus.PENDING)) { return false; }
        
        inventoryService.deleteOrderAndRestoreStock(order.getId());
        return true;
    }

}
