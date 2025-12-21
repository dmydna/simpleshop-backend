package com.techlab.store.dto;

import com.techlab.store.entity.Order;
import com.techlab.store.entity.Product;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Long id;
    private ProductDetail product;
    private int quantity;

    @Data
    public static class ProductDetail{
        Long id;
        String title;
        Integer stock;
        Double price;
    }
}
