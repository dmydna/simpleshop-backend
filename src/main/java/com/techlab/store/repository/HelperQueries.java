package com.techlab.store.repository;

public class HelperQueries {


	public static String genStatsField(String tableName, String nameField, int limit) {
        return String.format(
            "SELECT %s AS field_value, COUNT(*) as count " +
            "FROM %s " +
            "GROUP BY %s " +
            "ORDER BY count " +
            "DESC LIMIT %d",
            nameField, tableName, nameField, limit
        );
    }

    public static String genRelStatsField(
            String primaryTableName,    // Ej: "listings"
            String secondaryTableName,  // Ej: "product_tags"
            String primaryForeignKey,   // Ej: "product_id" (columna en la tabla principal)
            String secondaryPrimaryKey, // Ej: "product_id" o "id" (columna en la secundaria)
            String nameField,           // Ej: "tags" (el campo que quieres agrupar)
            int limit) {
            
        return String.format(
                "SELECT r.%s AS field_value, "+
                "COUNT(t.id) as count " +
                "FROM %s t " +
                "JOIN %s r ON t.%s = r.%s " + 
                "GROUP BY r.%s " +
                "ORDER BY count DESC " +
                "LIMIT %d",
                nameField,             // 1. Para r.%s (SELECT)
                primaryTableName,      // 2. Para FROM %s t
                secondaryTableName,    // 3. Para JOIN %s r
                primaryForeignKey,     // 4. Para t.%s (ej: t.product_id)
                secondaryPrimaryKey,   // 5. Para r.%s (ej: r.product_id o r.id)
                nameField,             // 6. Para GROUP BY r.%s
                limit                  // 7. Para LIMIT %d
        );
    }


}