package com.techlab.store.service;

import org.springframework.stereotype.Service;

import com.techlab.store.entity.Listing;
import com.techlab.store.mapper.ListingMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final JdbcTemplate jdbcTemplate;

    // 1. Top Sales (Top N Listing)
    public List<Listing> getTopSales(int limit) {
        String sql = """
            SELECT l.*
            FROM listings l
            JOIN order_items oi ON l.id = oi.listing_id
            GROUP BY l.id
            ORDER BY SUM(oi.quantity) DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Listing.class), limit);
    }


    // 1. Top Rated (Top N Listing)
    public List<Listing> getTopRated(int limit) {
        String sql = """
            SELECT l.*
            FROM listings l
            JOIN products p ON l.product_id = p.id
            ORDER BY p.rating DESC
            LIMIT ?
            """;
    
        // Usamos BeanPropertyRowMapper para mapear automáticamente a la entidad Listing
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Listing.class), limit);
    }


    // 1. Top ON-Sale (Top N Listing)
    public List<Listing> getTopOnsale(int limit) {
        String sql = """
            SELECT l.*
            FROM listings l
            ORDER BY l.discount_percentage DESC
            LIMIT ?
            """;
    
        // Usamos BeanPropertyRowMapper para mapear automáticamente a la entidad Listing
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Listing.class), limit);
    }



    // 1. Top Visits (Top N Listing)
    public List<Listing> getTopVisit(int limit) {
        String sql = """
            SELECT l.*
            FROM listings l
            ORDER BY l.visits DESC
            LIMIT ?
            """;
    
        // Usamos BeanPropertyRowMapper para mapear automáticamente a la entidad Listing
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Listing.class), limit);
    }


    // 1. Top Tags (Top N)
    public List<Map<String, Object>> getPopularTags(int limit) {
        String sql = "SELECT tags, COUNT(*) as count FROM product_tags GROUP BY tags ORDER BY count DESC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("name", rs.getString("tags"));
            row.put("count", rs.getLong("count"));
            return row;
        }, limit);
    }

    // 2. Categorías Populares (Asumiendo que tienes una tabla 'categories' o
    // columna en 'listings')
    public List<Map<String, Object>> getPopularCategories(int limit) {
        // Ejemplo si la categoría está en la tabla 'listings'
        String sql = "SELECT category, COUNT(*) as count FROM products GROUP BY category ORDER BY count DESC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("name", rs.getString("category"));
            row.put("count", rs.getLong("count"));
            return row;
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
            users.put("total",  rs.getLong("total_users"));
            stats.put("users", users);

            // statas.orders
            Map<String, Object> orders = new HashMap<>();
            orders.put("paid", rs.getLong("total_orders_paid")); 
            orders.put("pending", rs.getLong("total_orders_pending")); 
            orders.put("total", rs.getLong("total_orders")); 
            stats.put("orders", orders);

            // statas.products
            Map<String, Object> products = new HashMap<>();
            products.put("draft",  rs.getLong("total_products_draft")); 
            products.put("active", rs.getLong("total_products_active"));
            products.put("total",  rs.getLong("total_products")); 
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