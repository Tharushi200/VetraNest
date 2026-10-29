package com.vetranest.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Pattern implementation for Managing MySQL Database Connections.
 * Ensures only a single, thread-safe connection instance is shared throughout
 * the application lifecycle.
 *
 * Pattern: Singleton Design Pattern (Enterprise Application Standards)
 */
public class DatabaseConnection {

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private String url;
    private String user;
    private String password;
    private String driver;

    // Private constructor prevents direct instantiation
    private DatabaseConnection() {
        loadProperties();
        initConnection();
    }

    /**
     * Reads database configuration from db.properties in classpath.
     * Uses robust fallbacks if file is missing.
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("database/db.properties")) {
            if (in != null) {
                props.load(in);
                this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/vetranest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                this.user = props.getProperty("db.user", "root");
                this.password = props.getProperty("db.password", "root");
            } else {
                applyFallbackProperties();
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Warning: Could not read db.properties. Using fallback defaults: " + e.getMessage());
            applyFallbackProperties();
        }
    }

    private void applyFallbackProperties() {
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://localhost:3307/vetranest_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        this.user = "root";
        this.password = "";
    }

    /**
     * Initializes the JDBC Driver and establishes connection.
     */
    private void initConnection() {
        try {
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("[DatabaseConnection] Connection established successfully to " + url);
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Connection failed: " + e.getMessage());
        }
    }

    /**
     * Double-Checked Locking Thread-Safe Singleton Accessor.
     *
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Returns active database connection. Re-establishes connection if closed.
     *
     * @return java.sql.Connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(url, user, password);
                }
            }
        }
        return connection;
    }

    /**
     * Tests if the database connection is currently alive and active.
     *
     * @return true if valid and connected, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && conn.isValid(3);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Updates active credentials at runtime if needed.
     */
    public synchronized void updateCredentials(String user, String password) {
        this.user = user;
        this.password = password;
        closeConnection();
        initConnection();
    }

    /**
     * Safely closes the active connection.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                System.out.println("[DatabaseConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
