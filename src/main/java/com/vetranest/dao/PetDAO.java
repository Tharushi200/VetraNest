package com.vetranest.dao;

import com.vetranest.config.DatabaseConnection;
import com.vetranest.model.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pet CRUD operations and owner joins.
 */
public class PetDAO {

    private final DatabaseConnection dbConnection;

    public PetDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Pet> getAllPets() throws SQLException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS owner_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.customer_id " +
                     "ORDER BY p.pet_id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Pet> searchPets(String keyword, String speciesFilter) throws SQLException {
        List<Pet> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.name AS owner_name " +
                "FROM pets p " +
                "JOIN customers c ON p.customer_id = c.customer_id " +
                "WHERE 1=1 "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasSpecies = speciesFilter != null && !speciesFilter.trim().isEmpty() && !speciesFilter.equalsIgnoreCase("All Species");

        if (hasKeyword) {
            sql.append("AND (LOWER(p.pet_code) LIKE ? OR LOWER(p.pet_name) LIKE ? OR LOWER(p.breed) LIKE ? OR LOWER(c.name) LIKE ?) ");
        }
        if (hasSpecies) {
            sql.append("AND LOWER(p.species) = LOWER(?) ");
        }

        sql.append("ORDER BY p.pet_id ASC");

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (hasKeyword) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
            }
            if (hasSpecies) {
                stmt.setString(paramIndex++, speciesFilter.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    public List<Pet> getPetsByCustomerId(int customerId) throws SQLException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS owner_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.customer_id " +
                     "WHERE p.customer_id = ? " +
                     "ORDER BY p.pet_id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    public Pet getPetById(int id) throws SQLException {
        String sql = "SELECT p.*, c.name AS owner_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.customer_id " +
                     "WHERE p.pet_id = ?";
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

    public boolean insertPet(Pet p) throws SQLException {
        if (p.getPetCode() == null || p.getPetCode().trim().isEmpty()) {
            p.setPetCode(getNextPetCode());
        }

        String sql = "INSERT INTO pets (pet_code, customer_id, pet_name, species, breed, gender, dob, weight, color) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getPetCode());
            stmt.setInt(2, p.getCustomerId());
            stmt.setString(3, p.getPetName().trim());
            stmt.setString(4, p.getSpecies() != null ? p.getSpecies().trim() : "Dog");
            stmt.setString(5, p.getBreed() != null ? p.getBreed().trim() : "Mixed");
            stmt.setString(6, p.getGender() != null ? p.getGender().trim() : "Male");
            stmt.setDate(7, p.getDob());
            stmt.setDouble(8, p.getWeight());
            stmt.setString(9, p.getColor() != null ? p.getColor().trim() : "");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setPetId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updatePet(Pet p) throws SQLException {
        String sql = "UPDATE pets SET customer_id = ?, pet_name = ?, species = ?, breed = ?, " +
                     "gender = ?, dob = ?, weight = ?, color = ? WHERE pet_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCustomerId());
            stmt.setString(2, p.getPetName().trim());
            stmt.setString(3, p.getSpecies() != null ? p.getSpecies().trim() : "Dog");
            stmt.setString(4, p.getBreed() != null ? p.getBreed().trim() : "Mixed");
            stmt.setString(5, p.getGender() != null ? p.getGender().trim() : "Male");
            stmt.setDate(6, p.getDob());
            stmt.setDouble(7, p.getWeight());
            stmt.setString(8, p.getColor() != null ? p.getColor().trim() : "");
            stmt.setInt(9, p.getPetId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletePet(int id) throws SQLException {
        String sql = "DELETE FROM pets WHERE pet_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public String getNextPetCode() {
        String sql = "SELECT MAX(pet_id) FROM pets";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int nextId = rs.getInt(1) + 1;
                return String.format("P%03d", nextId);
            }
        } catch (SQLException e) {
            System.err.println("[PetDAO] Error generating pet code: " + e.getMessage());
        }
        return "P001";
    }

    private Pet mapResultSet(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setPetId(rs.getInt("pet_id"));
        p.setPetCode(rs.getString("pet_code"));
        p.setCustomerId(rs.getInt("customer_id"));
        p.setOwnerName(rs.getString("owner_name"));
        p.setPetName(rs.getString("pet_name"));
        p.setSpecies(rs.getString("species"));
        p.setBreed(rs.getString("breed"));
        p.setGender(rs.getString("gender"));
        p.setDob(rs.getDate("dob"));
        p.setWeight(rs.getDouble("weight"));
        p.setColor(rs.getString("color"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
