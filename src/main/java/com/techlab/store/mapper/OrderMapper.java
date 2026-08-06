package com.techlab.store.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techlab.store.dto.CreateOrderDTO;
import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.OrderItemDto;
import com.techlab.store.dto.OrderSummary;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderItem;


@Mapper(
    componentModel = "spring", 
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
    uses = {OrderMappingHelper.class})
public interface OrderMapper {

    // --- ENTITY -> DTO ---
    @Mapping(target = "meta.createdAt", source = "createdAt")
    @Mapping(target = "meta.updatedAt", source = "updatedAt")
    @Mapping(target = "meta.deletedAt", source = "deletedAt")
    @Mapping(target = "totalQuantity", source = "items", 
    qualifiedByName = "calculateTotalQuantity")
    OrderComplete toFullDto(Order entity);

    @Mapping(target = "meta.createdAt", source = "createdAt")
    @Mapping(target = "meta.updatedAt", source = "updatedAt")
    @Mapping(target = "meta.deletedAt", source = "deletedAt")
    @Mapping(target = "totalQuantity",  source = "items", 
    qualifiedByName = "calculateTotalQuantity")
    OrderSummary toSummaryDto(Order order);


    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "productId", source = "listing.product.id")
    @Mapping(target = "discountPercentageAtPurchase", source="discountPercentageAtPurchase")
    @Mapping(target = "reviewId", source = "review.id")
    @Mapping(target = "rating", source = "review.rating")
    @Mapping(target = "name", source = "listing.product.name")
    @Mapping(target = "thumbnail", source = "listing.thumbnail")
    @Mapping(target = "stock", source = "listing.stock")
    @Mapping(target = "status", source = "order.status", qualifiedByName = "statusToString")
    @Mapping(target = "createdAt", source = "order.createdAt")
    OrderItemDto toItemDto(OrderItem entity);

    // --- DTO -> ENTITY ---
    @Mapping(target = "createdAt", source = "meta.createdAt")
    @Mapping(target = "updatedAt", source = "meta.updatedAt")
    @Mapping(target = "deletedAt", source = "meta.deletedAt")
    @Mapping(target = "client", ignore = true)
    Order toEntity(OrderComplete dto);

    // Al compilar, MapStruct buscará un método que convierta OrderItemDto -> OrderItem
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "failedItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Order toEntity(CreateOrderDTO dto, Client client);

    // Al llamarse igual que el tipo de la lista, MapStruct lo asocia automáticamente
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "listing.id", source = "listingId")
    @Mapping(target = "priceAtPurchase", source = "priceAtPurchase")
    @Mapping(target = "quantity", source = "quantity")
    OrderItem toOrderItem(OrderItemDto dto);

    // --- LISTAS ---
    List<OrderComplete> toFullDtoList(List<Order> orders);
    List<OrderItem> toOrderItemList(List<OrderItemDto> items);
    List<OrderItemDto> toItemDtoList(List<OrderItem> items);

    // --- ACTUALIZACIONES ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateOrderFromDto(OrderComplete dto, @MappingTarget Order entity);

    @Mapping(target = "id", ignore = true)
    void updateFromEntity(Order update, @MappingTarget Order order);

}