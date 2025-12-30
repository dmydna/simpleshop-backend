package com.techlab.store.utils;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ListingMapper {

    @Mapping(source = "product.id", target = "product_id")
    @Mapping(source = "product.sku", target = "sku")
    @Mapping(source = "product.stock", target = "stock")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.weight", target = "weight")
    @Mapping(source = "product.category", target = "category")
    @Mapping(source = "product.tags", target = "tags" )
    @Mapping(source = "product.dimensions", target = "dimensions")
    @Mapping(source = "product.meta", target = "meta")
    ListingDTO toDto(Listing listing);
    @InheritInverseConfiguration
    Listing toEntity(ListingDTO dto);
    List<ListingDTO> toDtoList(List<Listing> listings);
    List<Listing> toEntityList(List<ListingDTO> listings);
}
