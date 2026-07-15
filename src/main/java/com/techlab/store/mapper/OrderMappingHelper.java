package com.techlab.store.mapper;

import java.math.BigDecimal;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.techlab.store.dto.CreateOrderDTO;
import com.techlab.store.dto.OrderItemDto;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.enums.OrderStatus;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.service.ListingService;
import com.techlab.store.utils.EnumUtils;
import com.techlab.store.utils.HashUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMappingHelper {

	private final ListingService listingService;

    @AfterMapping
    public void orderAfterMapping(CreateOrderDTO dto, @MappingTarget Order order) {
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(java.time.LocalDateTime.now());
        for(OrderItem item : order.getItems()){
        	item.setOrder(order);
        }
    }

    @AfterMapping
    public void orderItemAfterMapping(OrderItemDto dto, @MappingTarget OrderItem orderItem) {
        Listing listing = listingService.getById(dto.listingId());
        orderItem.setListing(listing);
    }
}