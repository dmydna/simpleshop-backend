package com.techlab.store.utils;

import com.techlab.store.dto.OrderFullDTO;
import com.techlab.store.dto.OrderSimpleDTO;
import com.techlab.store.entity.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {


    @Mapping(target = "client", source = "client") // Mapea el objeto completo
    OrderFullDTO toFullDto(Order entity);

    @Mapping(target = "client_id", source = "client.id")
    OrderSimpleDTO toSimpleDto(Order entity);

    //@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    void updateOrderFromDto(OrderFullDTO dto, @MappingTarget Order entity);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateOrderFromDto(OrderSimpleDTO dto, @MappingTarget Order entity);

    Order toEntity(OrderFullDTO dto);
    @Mapping(target = "client", ignore = true)
    Order toEntity(OrderSimpleDTO dto);



    List<OrderFullDTO> toFullDtoList(List<Order> orders);
}
