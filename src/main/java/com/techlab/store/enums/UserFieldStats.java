package com.techlab.store.enums;

import com.techlab.store.repository.HelperQueries;;

public enum UserFieldStats { 
    
    role("users", "role"),
    status("users", "status");

    private final String tableName;
    private final String nameField;

    UserFieldStats(String tableName, String nameField) {
        if (!tableName.matches("^[a-zA-Z0-9_]+$") || !nameField.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Nombre de tabla o campo inválido");
        }
        this.tableName = tableName;
        this.nameField = nameField;
    }
    
    public String getSql(int limit) {
        return HelperQueries.genStatsField(tableName, nameField, limit); 
    }
}