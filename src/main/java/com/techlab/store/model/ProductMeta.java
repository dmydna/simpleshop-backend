package com.techlab.store.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductMeta {
    private String barcode;
    private String qrCode;
}

