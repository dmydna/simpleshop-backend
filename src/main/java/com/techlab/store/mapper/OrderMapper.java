package com.techlab.store.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.OrderItemDto;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // --- ENTITY -> DTO ---
    OrderComplete toFullDto(Order entity);

    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "name", source = "listing.product.name")
    @Mapping(target = "stock", source = "listing.stock")
    OrderItemDto toDetailDto(OrderItem entity);

    // --- DTO -> ENTITY ---
    Order toEntity(OrderComplete dto);

    @Mapping(target = "listing.id", source = "listingId")
    OrderItem toDetailEntity(OrderItemDto dto);

    // --- LISTAS ---
    List<OrderComplete> toFullDtoList(List<Order> orders);
    List<OrderItem> toDetailDtoList(List<OrderItemDto> details);

    // --- ACTUALIZACIONES ---
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true) // Evita problemas con colecciones en updates
    void updateOrderFromDto(OrderComplete dto, @MappingTarget Order entity);
}