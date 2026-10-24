package com.techlab.store.enums;

import com.techlab.store.repository.HelperQueries;

public enum ListingFieldStats {
    
    availabilitystatus("listings", null, null, null, "availability_status"),
    status("listings", null, null, null, "status"),
    tags("listings", "product_tags", "product_id", "product_id", "tags"),
    category("listings", "products", "product_id", "id", "category");

    private final String fstTB;
    private final String sndTB;
    private final String fstFK;
    private final String sndPK;
    private final String field;

    ListingFieldStats(String fstTB, String sndTB, String fstFK, String sndPK, String field) {
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
              .genRelStatsField(fstTB, sndTB,fstFK, sndPK, field, limit);
        } else {
            // Caso normal sin JOIN
            return  HelperQueries
              .genStatsField(fstTB, field, limit);
        }
    }
}