package com.techlab.store.mapper;

import com.techlab.store.dto.OrderDetailDTO;
import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.entity.Order;
import com.techlab.store.entity.OrderDetail;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // --- ENTITY -> DTO ---
    OrderFullDTO toFullDto(Order entity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "stock", source = "product.stock")
    OrderDetailDTO toDetailDto(OrderDetail entity);

    // --- DTO -> ENTITY ---
    Order toEntity(OrderFullDTO dto);

    @Mapping(target = "product.id", source = "productId")
    OrderDetail toDetailEntity(OrderDetailDTO dto);

    // --- LISTAS ---
    List<OrderFullDTO> toFullDtoList(List<Order> orders);
    List<OrderDetailDTO> toDetailDtoList(List<OrderDetail> details);

    // --- ACTUALIZACIONES ---
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "details", ignore = true) // Evita problemas con colecciones en updates
    void updateOrderFromDto(OrderFullDTO dto, @MappingTarget Order entity);
}