package com.vetranest.dao;

import com.vetranest.config.DatabaseConnection;
import com.vetranest.model.Treatment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Treatment (clinical records) CRUD and multi-table queries.
 */
public class TreatmentDAO {

    private final DatabaseConnection dbConnection;

    public TreatmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Treatment> getAllTreatments() throws SQLException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT t.*, p.pet_name, p.species AS pet_species, c.name AS owner_name " +
                     "FROM treatments t " +
                     "JOIN pets p ON t.pet_id = p.pet_id " +
                     "JOIN customers c ON p.customer_id = c.customer_id " +
                     "ORDER BY t.treatment_date DESC, t.treatment_id DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Treatment> getTreatmentsByPet(int petId) throws SQLException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT t.*, p.pet_name, p.species AS pet_species, c.name AS owner_name " +
                     "FROM treatments t " +
                     "JOIN pets p ON t.pet_id = p.pet_id " +
                     "JOIN customers c ON p.customer_id = c.customer_id " +
                     "WHERE t.pet_id = ? " +
                     "ORDER BY t.treatment_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, petId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean insert(Treatment t) throws SQLException {
        String sql = "INSERT INTO treatments (appointment_id, pet_id, treatment_date, diagnosis, medication, instructions, doctor_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (t.getAppointmentId() > 0) {
                stmt.setInt(1, t.getAppointmentId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setInt(2, t.getPetId());
            stmt.setDate(3, t.getTreatmentDate() != null ? t.getTreatmentDate() : new Date(System.currentTimeMillis()));
            stmt.setString(4, t.getDiagnosis() != null ? t.getDiagnosis().trim() : "");
            stmt.setString(5, t.getMedication() != null ? t.getMedication().trim() : "");
            stmt.setString(6, t.getInstructions() != null ? t.getInstructions().trim() : "");
            stmt.setString(7, t.getDoctorName() != null ? t.getDoctorName().trim() : "Dr. K. Perera");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) t.setTreatmentId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean insert(Treatment t, double cost) throws SQLException {
        boolean ok = insert(t);
        if (ok && cost > 0) {
            recordBilling(t, cost);
        }
        return ok;
    }

    private void recordBilling(Treatment t, double cost) {
        String billCode = "B" + (System.currentTimeMillis() % 100000);
        int customerId = 1;
        String custSql = "SELECT customer_id FROM pets WHERE pet_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement cs = conn.prepareStatement(custSql)) {
            cs.setInt(1, t.getPetId());
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) customerId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[TreatmentDAO] Could not find customer for pet: " + e.getMessage());
        }

        String billSql = "INSERT INTO billings (bill_code, appointment_id, customer_id, service_fee, medication_fee, discount, total_amount, payment_status, payment_method) " +
                         "VALUES (?, ?, ?, ?, 0.00, 0.00, ?, 'Paid', 'Cash')";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(billSql)) {
            stmt.setString(1, billCode);
            if (t.getAppointmentId() > 0) {
                stmt.setInt(2, t.getAppointmentId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, customerId);
            stmt.setDouble(4, cost);
            stmt.setDouble(5, cost);
            stmt.executeUpdate();
            System.out.println("[TreatmentDAO] Auto-recorded billing " + billCode + " with amount LKR " + cost);
        } catch (SQLException e) {
            System.err.println("[TreatmentDAO] Error creating billing record: " + e.getMessage());
        }
    }

    public boolean update(Treatment t) throws SQLException {
        String sql = "UPDATE treatments SET treatment_date=?, diagnosis=?, medication=?, instructions=?, doctor_name=? " +
                     "WHERE treatment_id=?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, t.getTreatmentDate());
            stmt.setString(2, t.getDiagnosis() != null ? t.getDiagnosis().trim() : "");
            stmt.setString(3, t.getMedication() != null ? t.getMedication().trim() : "");
            stmt.setString(4, t.getInstructions() != null ? t.getInstructions().trim() : "");
            stmt.setString(5, t.getDoctorName() != null ? t.getDoctorName().trim() : "Dr. K. Perera");
            stmt.setInt(6, t.getTreatmentId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setAppointmentId(rs.getInt("appointment_id"));
        t.setPetId(rs.getInt("pet_id"));
        t.setPetName(rs.getString("pet_name"));
        t.setPetSpecies(rs.getString("pet_species"));
        t.setOwnerName(rs.getString("owner_name"));
        t.setTreatmentDate(rs.getDate("treatment_date"));
        t.setDiagnosis(rs.getString("diagnosis"));
        t.setMedication(rs.getString("medication"));
        t.setInstructions(rs.getString("instructions"));
        t.setDoctorName(rs.getString("doctor_name"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
