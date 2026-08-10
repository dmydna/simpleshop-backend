package com.techlab.store.service;

import lombok.RequiredArgsConstructor;

import com.techlab.store.dto.FieldStats;
import com.techlab.store.entity.Listing;
import com.techlab.store.repository.ListingQueries;

import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final JdbcTemplate jdbcTemplate;

    // 1. Top Sales (Top N Listing)
    public List<Listing> getTopSales(int limit) {
        return jdbcTemplate.query(
            ListingQueries.GET_TOP_SALES_SQL, 
            new BeanPropertyRowMapper<>(ListingQueries.LISTING_CLASS), 
            limit
        );
    }

    // 1. Top Rated (Top N Listing)
    public List<Listing> getTopRated(int limit) {
        // Usamos BeanPropertyRowMapper para mapear automáticamente a la entidad Listing
        return jdbcTemplate.query(
            ListingQueries.GET_TOP_RATED_SQL, 
            new BeanPropertyRowMapper<>(ListingQueries.LISTING_CLASS), 
            limit
        );
    }

    // 1. Top ON-Sale (Top N Listing)
    public List<Listing> getTopOnsale(int limit) {
        return jdbcTemplate.query(
            ListingQueries.GET_TOP_ONSALES_SQL, 
            new BeanPropertyRowMapper<>(ListingQueries.LISTING_CLASS), 
            limit
        );
    }

    // 1. Top Visits (Top N Listing)
    public List<Listing> getTopVisit(int limit) {
        return jdbcTemplate.query(
            ListingQueries.GET_TOP_VISITS_SQL, 
            new BeanPropertyRowMapper<>(ListingQueries.LISTING_CLASS)
            , limit
        );
    }



    public List<FieldStats> getStatsByField(
            String tableName,
            String nameField,
            int limit) {

        if (!tableName.matches("^[a-zA-Z0-9_]+$")
                || !nameField.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Nombre de tabla o campo inválido");
        }

        String sql = String.format(
                "SELECT %s, COUNT(*) as count " +
                        "FROM %s " +
                        "GROUP BY %s " +
                        "ORDER BY count " +
                        "DESC LIMIT ?",
                nameField, tableName, nameField);

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new FieldStats(
                    rs.getString(nameField),
                    rs.getLong("count"));
        }, limit);
    }

    // 3. Estadísticas Generales (Ej: Total listings, total ventas, etc.)
    public Map<String, Object> getGeneralStats() {
        String sql = "SELECT " +
        // GENERAL
                "  COALESCE((SELECT SUM(total_amount) FROM orders WHERE status = 'PAID'), 0) as total_sales," +
                "  COALESCE((SELECT COUNT(*) FROM listings WHERE status != 'DELETED'), 0) as total_listings, " +
                "  COALESCE((SELECT SUM(price) FROM listings WHERE status = 'ACTIVE'), 0) as total_listing_value, " +
                // ORDERS
                "  COALESCE((SELECT COUNT(*) FROM orders), 0) as total_orders, " +
                "  COALESCE((SELECT COUNT(*) FROM orders WHERE status = 'PAID'), 0) as total_orders_paid, " +
                "  COALESCE((SELECT COUNT(*) FROM orders WHERE status = 'PENDING'), 0) as total_orders_pending, " +
                // PRODUCTS
                "  COALESCE((SELECT COUNT(*) FROM products), 0) as total_products, " +
                "  COALESCE((SELECT COUNT(*) FROM products WHERE status = 'DRAFT'), 0) as total_products_draft, " +
                "  COALESCE((SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'), 0) as total_products_active, " +
                // REVIEWS
                "  COALESCE((SELECT COUNT(*) FROM reviews), 0) as total_reviews, " +
                "  COALESCE((SELECT COUNT(*) FROM reviews WHERE status = 'ACTIVE'), 0) as total_reviews_active, " +
                "  COALESCE((SELECT COUNT(*) FROM reviews WHERE status = 'PENDING'), 0) as total_reviews_pending, " +
                // USERS
                "  COALESCE((SELECT COUNT(*) FROM users), 0) as total_users, " +
                "  COALESCE((SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'), 0) as total_user_active," +
                "  COALESCE((SELECT COUNT(*) FROM users WHERE status = 'BANNED'), 0) as total_user_banned";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalListings", rs.getLong("total_listings"));
            stats.put("totalListingValue", rs.getBigDecimal("total_listing_value"));
            stats.put("totalSales", rs.getBigDecimal("total_sales"));

            // stats.user
            Map<String, Object> users = new HashMap<>();
            users.put("active", rs.getLong("total_user_active"));
            users.put("banned", rs.getLong("total_user_banned"));
            users.put("total", rs.getLong("total_users"));
            stats.put("users", users);

            // stats.reviews
            Map<String, Object> reviews = new HashMap<>();
            reviews.put("total", rs.getLong("total_reviews"));
            reviews.put("active", rs.getLong("total_reviews_active"));
            reviews.put("pending", rs.getLong("total_reviews_pending"));
            stats.put("reviews", reviews);

            // statas.orders
            Map<String, Object> orders = new HashMap<>();
            orders.put("paid", rs.getLong("total_orders_paid"));
            orders.put("pending", rs.getLong("total_orders_pending"));
            orders.put("total", rs.getLong("total_orders"));
            stats.put("orders", orders);

            // statas.products
            Map<String, Object> products = new HashMap<>();
            products.put("draft", rs.getLong("total_products_draft"));
            products.put("active", rs.getLong("total_products_active"));
            products.put("total", rs.getLong("total_products"));
            stats.put("products", products);

            return stats;
        });
    }

    // 4. Tags duplicadas
    public List<Map<String, Object>> getDuplicateTags() {
        String sql = "SELECT name, COUNT(*) as count FROM tags GROUP BY name HAVING COUNT(*) > 1";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("name", rs.getString("name"));
            row.put("count", rs.getLong("count"));
            return row;
        });
    }
}
