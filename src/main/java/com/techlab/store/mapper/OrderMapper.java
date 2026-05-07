package com.techlab.store.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.techlab.store.dto.CreateOrderDTO;
import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.OrderItemDto;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;
import com.techlab.store.enums.OrderStatus;
import org.mapstruct.*;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    // CHECKME se implementa CreateOrderDTO

    // --- ENTITY -> DTO ---
    @Mapping(target = "meta.createdAt", source = "createdAt")
    @Mapping(target = "meta.updatedAt", source = "updatedAt")
    @Mapping(target = "meta.deletedAt", source = "deletedAt")
    OrderComplete toFullDto(Order entity);

    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "name", source = "listing.product.name")
    @Mapping(target = "stock", source = "listing.stock")
    OrderItemDto toItemDto(OrderItem entity);

    // --- DTO -> ENTITY ---
    Order toEntity(OrderComplete dto);
    Order toEntity(CreateOrderDTO dto);

    @Mapping(target = "listing.id", source = "listingId")
    OrderItem toDetailEntity(OrderItemDto dto);

    // --- LISTAS ---
    List<OrderComplete> toFullDtoList(List<Order> orders);
    List<OrderItem> toItemList(List<OrderItemDto> items);
    List<OrderItemDto> toItemDtoList(List<OrderItem> items);

    // --- ACTUALIZACIONES ---
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true) // Evita problemas con colecciones en updates
    void updateOrderFromDto(OrderComplete dto, @MappingTarget Order entity);


    @Mapping(target = "id", ignore = true)
    void updateFromEntity(Order update, @MappingTarget Order order);


    @AfterMapping
    default void orderAfterMapping(
        CreateOrderDTO dto, 
        @MappingTarget Order order)
    {
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(java.time.LocalDateTime.now());
    }
}