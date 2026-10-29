package com.vetranest.dao;

import com.vetranest.config.DatabaseConnection;
import com.vetranest.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Customer persistence & queries.
 * DAO Design Pattern implementation.
 */
public class CustomerDAO {

    private final DatabaseConnection dbConnection;

    public CustomerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE LOWER(customer_code) LIKE ? OR LOWER(name) LIKE ? " +
                     "OR LOWER(email) LIKE ? OR phone LIKE ? ORDER BY customer_id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean insertCustomer(Customer c) throws SQLException {
        if (c.getCustomerCode() == null || c.getCustomerCode().trim().isEmpty()) {
            c.setCustomerCode(getNextCustomerCode());
        }

        String sql = "INSERT INTO customers (customer_code, name, email, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getCustomerCode());
            stmt.setString(2, c.getName().trim());
            stmt.setString(3, c.getEmail().trim());
            stmt.setString(4, c.getPhone().trim());
            stmt.setString(5, c.getAddress() != null ? c.getAddress().trim() : "");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setCustomerId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getName().trim());
            stmt.setString(2, c.getEmail().trim());
            stmt.setString(3, c.getPhone().trim());
            stmt.setString(4, c.getAddress() != null ? c.getAddress().trim() : "");
            stmt.setInt(5, c.getCustomerId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public String getNextCustomerCode() {
        String sql = "SELECT MAX(customer_id) FROM customers";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("C%03d", nextId);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error generating code: " + e.getMessage());
        }
        return "C001";
    }

    private Customer mapResultSet(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setCustomerCode(rs.getString("customer_code"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
