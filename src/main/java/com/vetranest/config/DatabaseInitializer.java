package com.vetranest.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Automatically initializes database schema and seed data from schema.sql
 * if tables have not been created yet.
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        System.out.println("[DatabaseInitializer] Checking and initializing database schema...");
        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database/schema.sql")) {
            if (in == null) {
                System.err.println("[DatabaseInitializer] schema.sql not found in resources!");
                return false;
            }

            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getConnection();
            if (conn == null) {
                System.err.println("[DatabaseInitializer] Cannot get database connection.");
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                 Statement stmt = conn.createStatement()) {

                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String query = sql.toString().trim();
                        // Execute statement
                        try {
                            stmt.execute(query);
                        } catch (Exception ex) {
                            // Non-fatal if table already exists or duplicate key
                            System.out.println("[DatabaseInitializer] SQL Notice: " + ex.getMessage());
                        }
                        sql.setLength(0);
                    }
                }
                System.out.println("[DatabaseInitializer] Database schema & seed verification completed successfully.");
                return true;
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Initialization error: " + e.getMessage());
            return false;
        }
    }
}
