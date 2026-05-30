package com.taxiapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized database configuration.
 * 
 * ⚠️ IMPORTANT: This file should NOT contain your actual credentials!
 * 
 * SETUP INSTRUCTIONS:
 * 1. Copy DBConfig.template.java to create this file
 * 2. Fill in your actual database credentials
 * 3. This file is in .gitignore and will NOT be tracked by Git
 * 
 * Update DB_URL, DB_USER, and DB_PASSWORD to match your local MySQL setup.
 */
public class DBConfig {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/TaxiOrderingSystem?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";  // Fill in your actual password

    /** Returns a new database connection. Callers must close it (use try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
