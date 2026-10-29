package com.vetranest.dao;

import com.vetranest.config.DatabaseConnection;
import com.vetranest.exception.DuplicateUserException;
import com.vetranest.model.User;
import com.vetranest.util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for User CRUD operations.
 * Implements DAO pattern for separation of data persistence logic.
 */
public class UserDAO {

    private final DatabaseConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Checks if a username already exists in the database.
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Creates a new user record. Throws DuplicateUserException if username exists.
     */
    public boolean create(User user) throws SQLException, DuplicateUserException {
        if (usernameExists(user.getUsername())) {
            throw new DuplicateUserException(user.getUsername());
        }

        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, security_question, security_answer) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName().trim());
            stmt.setString(4, user.getEmail().trim());
            stmt.setString(5, user.getRole() != null ? user.getRole() : "Admin");
            stmt.setString(6, user.getSecurityQuestion() != null ? user.getSecurityQuestion() : "What is the clinic license number?");
            stmt.setString(7, user.getSecurityAnswer() != null ? user.getSecurityAnswer().trim() : "VN2026");

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Authenticates a user by username and plain text password.
     */
    public User authenticate(String username, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordHasher.verify(plainPassword, storedHash)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Resets a user's password if the security answer matches.
     */
    public boolean resetPassword(String username, String securityAnswer, String newPassword) throws SQLException {
        String verifySql = "SELECT user_id, security_answer FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
            verifyStmt.setString(1, username.trim());
            try (ResultSet rs = verifyStmt.executeQuery()) {
                if (rs.next()) {
                    String storedAnswer = rs.getString("security_answer");
                    if (storedAnswer != null && storedAnswer.equalsIgnoreCase(securityAnswer.trim())) {
                        String updateSql = "UPDATE users SET password_hash = ? WHERE LOWER(username) = LOWER(?)";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, PasswordHasher.hash(newPassword));
                            updateStmt.setString(2, username.trim());
                            return updateStmt.executeUpdate() > 0;
                        }
                    }
                }
            }
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
