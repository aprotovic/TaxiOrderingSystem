package com.taxiapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TEMPLATE FILE: Rename this to DBConfig.java and fill in your credentials.
 * 
 * Centralized database configuration.
 * Update DB_URL, DB_USER, and DB_PASSWORD to match your local MySQL setup.
 * 
 * ⚠️ SECURITY: Never commit actual credentials to Git!
 * Keep DBConfig.java in .gitignore to prevent accidental leaks.
 */
public class DBConfig {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/TaxiOrderingSystem?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";  // Change to your MySQL username
    private static final String DB_PASS = "";      // Change to your MySQL password

    /** Returns a new database connection. Callers must close it (use try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
