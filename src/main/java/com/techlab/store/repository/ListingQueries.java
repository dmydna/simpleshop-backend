package com.techlab.store.repository;

import com.techlab.store.entity.Listing;

public class ListingQueries {



    public static final String GET_TOP_RATED_SQL = 
        "SELECT l.* " +
        "FROM listings l " +
        "JOIN products p ON l.product_id = p.id " +
        "WHERE l.status = 'ACTIVE' " +
        "ORDER BY p.rating DESC " +
        "LIMIT ?";


    public static final String GET_TOP_SALES_SQL = """
        SELECT l.*
        FROM listings l
        JOIN order_items oi ON l.id = oi.listing_id
        WHERE l.status = 'ACTIVE'
        GROUP BY l.id
        ORDER BY SUM(oi.quantity) DESC
        LIMIT ?
        """;

    public static final String GET_TOP_VISITS_SQL = """
        SELECT l.*
        FROM listings l
        WHERE l.status = 'ACTIVE'
        ORDER BY l.visits DESC
        LIMIT ?
        """;


    public static final String GET_TOP_ONSALES_SQL = """
        SELECT l.*
        FROM listings l
        WHERE l.status = 'ACTIVE'
        ORDER BY l.discount_percentage DESC
        LIMIT ?
        """;


    // Constante para el nombre de la clase de mapeo
    public static final Class<Listing> LISTING_CLASS = Listing.class;
}
