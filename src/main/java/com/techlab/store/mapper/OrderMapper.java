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


@Mapper(
    componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
    uses = {OrderMappingHelper.class})
public interface OrderMapper {

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

    // Al compilar, MapStruct buscará un método que convierta OrderItemDto -> OrderItem
    Order toEntity(CreateOrderDTO dto);

    // SOLUCIÓN: Cambiar el nombre de 'toDetailEntity' a 'toOrderItem'
    // Al llamarse igual que el tipo de la lista, MapStruct lo asocia automáticamente
    @Mapping(target = "listing.id", source = "listingId")
    @Mapping(target = "priceAtPurchase", source = "priceAtPurchase")
    @Mapping(target = "quantity", source = "quantity")
    OrderItem toOrderItem(OrderItemDto dto);

    // --- LISTAS ---
    List<OrderComplete> toFullDtoList(List<Order> orders);
    List<OrderItem> toOrderItemList(List<OrderItemDto> items);
    List<OrderItemDto> toItemDtoList(List<OrderItem> items);

    // --- ACTUALIZACIONES ---
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true) 
    void updateOrderFromDto(OrderComplete dto, @MappingTarget Order entity);

    @Mapping(target = "id", ignore = true)
    void updateFromEntity(Order update, @MappingTarget Order order);

}