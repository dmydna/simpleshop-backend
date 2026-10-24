package com.techlab.store.enums;

import com.techlab.store.repository.HelperQueries;

public enum ProductFieldStats {

    tags("products", "product_tags", "product_id", "product_id", "tags"),
    category("products", null, null, null, "category"), //Owner
    status("products", null, null, null, "status");     // Owner

    private final String fstTB;    // primaryTable
    private final String sndTB;    // secondaryTable (relation table)
    private final String fstFK;    // primaryFK      
    private final String sndPK;    // secondaryFK
    private final String field;    // nameField

    ProductFieldStats(String fstTB, String sndTB, String fstFK, String sndPK, String field) {
        this.fstTB = fstTB;
        this.sndTB = sndTB;
        this.fstFK = fstFK;
        this.sndPK = sndPK;
        this.field = field;
    }

    public String getSql(int limit) {
        if (sndTB != null) {
            // Caso con JOIN
            return HelperQueries
              .genRelStatsField(fstTB, sndTB, fstFK, sndPK, field, limit);
        } else {
            // Caso normal sin JOIN
            return  HelperQueries
              .genStatsField(fstTB, field, limit);
        }
    }



}