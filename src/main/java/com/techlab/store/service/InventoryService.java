package com.techlab.store.service;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ListingRepository listingRepository;
    private final OrderRepository orderRepository;
    
    @Transactional
    public boolean decreaseStock(Long listingId, Integer quantity) {
        Optional<Listing> listingOpt = listingRepository.findById(listingId);


        if (listingOpt.isEmpty()) {
            return false;
        }

        Listing listing = listingOpt.get();
        if (listing.getStock() < quantity) {
            return false;
        }

        if(listing.getStock() == 0){
            listing.setAvailabilityStatus("Out of Stock");
            listing.setStatus(ListingStatus.INACTIVE);
        }

        if(listing.getStock() < 10){
            listing.setAvailabilityStatus("Low Stock");
        }

        if(listing.getStock() >= 10){
            listing.setAvailabilityStatus("In Stock");
        }

        listing.setStock(listing.getStock() - quantity);
        listingRepository.save(listing);

        return true;
    }



    // NOTA La Restauracion de stock:
    // 1. Implica la eliminacion fisica de la mismo pedido.
    // 2. La activacion de la publicacion con stock restaurado.
    // public void deleteOrderAndRestoreStock(Long orderId)
    @Transactional
    public void deleteOrderAndRestoreStock(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);

        Order order = orderOpt.get();
        List<OrderItem> items = order.getItems();

        for (OrderItem deletedDetail : items) {
            Listing listing = deletedDetail.getListing();
            // restaura el stock completo del ítem eliminado
            listing.setStock(listing.getStock() + deletedDetail.getQuantity());
            if(!listing.getStatus().equals(ListingStatus.DELETED)){
                listing.setStatus(ListingStatus.ACTIVE);    
            }
            // listingRepository.save(listing);
        }
        orderRepository.delete(order);
    }


}