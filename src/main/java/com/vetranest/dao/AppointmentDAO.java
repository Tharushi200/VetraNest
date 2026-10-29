package com.vetranest.dao;

import com.vetranest.config.DatabaseConnection;
import com.vetranest.model.Appointment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointment CRUD and conflict detection.
 */
public class AppointmentDAO {

    private final DatabaseConnection dbConnection;

    public AppointmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Checks if any appointment already exists for the given date + time slot.
     * Used by AppointmentConflictException logic in controller.
     */
    public boolean isSlotBooked(Date date, String timeSlot, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments " +
                     "WHERE appointment_date = ? AND time_slot = ? AND status != 'Cancelled' " +
                     "AND appointment_id != ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            stmt.setString(2, timeSlot);
            stmt.setInt(3, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.pet_name, c.name AS customer_name " +
                     "FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.pet_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_id DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Appointment> searchAppointments(String keyword) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.pet_name, c.name AS customer_name " +
                     "FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.pet_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE LOWER(p.pet_name) LIKE ? OR LOWER(c.name) LIKE ? " +
                     "OR LOWER(a.service_type) LIKE ? OR LOWER(a.status) LIKE ? " +
                     "OR CAST(a.appointment_date AS CHAR) LIKE ? " +
                     "ORDER BY a.appointment_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String p = "%" + keyword.trim().toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) stmt.setString(i, p);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Appointment getById(int id) throws SQLException {
        String sql = "SELECT a.*, p.pet_name, c.name AS customer_name " +
                     "FROM appointments a " +
                     "JOIN pets p ON a.pet_id = p.pet_id " +
                     "JOIN customers c ON a.customer_id = c.customer_id " +
                     "WHERE a.appointment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean insert(Appointment a) throws SQLException {
        if (a.getAppointmentCode() == null || a.getAppointmentCode().isEmpty()) {
            a.setAppointmentCode(getNextCode());
        }
        String sql = "INSERT INTO appointments (appointment_code, customer_id, pet_id, " +
                     "appointment_date, time_slot, service_type, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, a.getAppointmentCode());
            stmt.setInt(2, a.getCustomerId());
            stmt.setInt(3, a.getPetId());
            stmt.setDate(4, a.getAppointmentDate());
            stmt.setString(5, a.getTimeSlot());
            stmt.setString(6, a.getServiceType());
            stmt.setString(7, a.getStatus() != null ? a.getStatus() : "Pending");
            stmt.setString(8, a.getNotes() != null ? a.getNotes() : "");
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) a.setAppointmentId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Appointment a) throws SQLException {
        String sql = "UPDATE appointments SET customer_id=?, pet_id=?, appointment_date=?, " +
                     "time_slot=?, service_type=?, status=?, notes=? WHERE appointment_id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, a.getCustomerId());
            stmt.setInt(2, a.getPetId());
            stmt.setDate(3, a.getAppointmentDate());
            stmt.setString(4, a.getTimeSlot());
            stmt.setString(5, a.getServiceType());
            stmt.setString(6, a.getStatus());
            stmt.setString(7, a.getNotes() != null ? a.getNotes() : "");
            stmt.setInt(8, a.getAppointmentId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public String getNextCode() {
        String sql = "SELECT MAX(appointment_id) FROM appointments";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return String.format("A%03d", rs.getInt(1) + 1);
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Error generating code: " + e.getMessage());
        }
        return "A001";
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentCode(rs.getString("appointment_code"));
        a.setCustomerId(rs.getInt("customer_id"));
        a.setCustomerName(rs.getString("customer_name"));
        a.setPetId(rs.getInt("pet_id"));
        a.setPetName(rs.getString("pet_name"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setTimeSlot(rs.getString("time_slot"));
        a.setServiceType(rs.getString("service_type"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
