package com.techlab.store.utils;

import com.techlab.store.dto.ClientDTO;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ProductDTO;
import com.techlab.store.entity.Client;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper{

    @Mapping(source = "product.id", target = "id")
    @Mapping(source = "product.brand", target = "brand")
    @Mapping(source = "product.stock", target = "stock" )
    @Mapping(source = "id", target="listingId" )
    ProductDTO toDto(Listing listing );
    @InheritInverseConfiguration
    Product toEntity(ProductDTO dto);
}
