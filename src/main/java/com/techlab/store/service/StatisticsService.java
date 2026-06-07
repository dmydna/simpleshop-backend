package com.techlab.store.service;

import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Tags Populares (Top N)
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
        String sql = "SELECT category, COUNT(*) as count FROM listings GROUP BY category ORDER BY count DESC LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            row.put("category", rs.getString("category"));
            row.put("count", rs.getLong("count"));
            return row;
        }, limit);
    }

    // 3. Estadísticas Generales (Ej: Total listings, total ventas, etc.)
    public Map<String, Object> getGeneralStats() {
        String sql = "SELECT " +
                "  (SELECT COUNT(*) FROM listings WHERE status != 'DELETED') as total_listings, " +
                "  COALESCE((SELECT SUM(price) FROM listings WHERE status = 'ACTIVE'), 0) as total_listing_value, " +
                "  COALESCE((SELECT SUM(total_amount) FROM orders WHERE status = 'PAID'), 0) as total_sales," +
                "  COALESCE((SELECT COUNT(*) FROM app_user WHERE status = 'ACTIVE'), 0) as total_user_active," +
                "  COALESCE((SELECT COUNT(*) FROM app_user WHERE status = 'BANNED'), 0) as total_user_banned";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalListings", rs.getLong("total_listings"));
            stats.put("totalListingValue", rs.getBigDecimal("total_listing_value"));
            stats.put("totalSales", rs.getBigDecimal("total_sales"));
            stats.put("totalActiveUser", rs.getBigDecimal("total_user_banned"));
            stats.put("totalBannedUser", rs.getBigDecimal("total_user_active"));

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