package com.techlab.store.mapper;

import java.math.BigDecimal;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.enums.Status;
import com.techlab.store.exceptions.CustomExceptions.ProductNotFoundException;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.service.PriceService;
import com.techlab.store.utils.EnumUtils;
import com.techlab.store.utils.HashUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListingMappingHelper {

    private final ProductRepository productRepository;
    private final PriceService priceService;

    @AfterMapping
    public void finishMapping(@MappingTarget Listing listing, CreateListingDTO dto) {
       // Saltamos validaciones y campos no necesarios para borrador
       // Nota: despues lo agregamos antes de publicar.
       if(dto.status() != null && dto.status().equals(ListingStatus.DRAFT)){
         
         log.info("🔔 Creando listing draft...");
         listing.setAvailabilityStatus("Pending");
         listing.setProduct(null);
         listing.setHash(HashUtil.generateShortHash());
         return;
       }
         Product existingProduct =  productRepository
                   .findBySku(dto.sku())
                   .orElseThrow(() -> new ProductNotFoundException());
         listing.setProduct(existingProduct);
         listing.getProduct().setStatus(Status.ACTIVE);
         listing.setStatus(ListingStatus.ACTIVE);
         listing.setAvailabilityStatus("In Stock");
         listing.setHash(HashUtil.generateShortHash());
    }


    @Named("statusToString")
    public String statusToString(ListingStatus status) {
        return EnumUtils.listingStatusToString(status);
    }

    @Named("stringToStatus")
    public ListingStatus stringToStatus(String str) {
        return EnumUtils.stringToListingStatus(str);
    }

    @AfterMapping
    public void linkReviewToParent(
            ReviewDTO reviewDto,
            @MappingTarget Review review,
            @Context Product parent) {
        review.setProduct(parent);
    }

    public BigDecimal calculateDiscount(Listing listing){
        BigDecimal price = listing.getPrice();
        Integer discount = listing.getDiscountPercentage();
        BigDecimal basePrice = (price != null) ? price  : BigDecimal.ZERO;
        Integer    discountPercent = (discount != null) ? discount : 0;
        return priceService
                 .calculateDiscount(basePrice, discountPercent)
                 .finalPrice();
    }
}