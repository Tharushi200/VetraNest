package com.vetranest.model;

import java.sql.Timestamp;

/**
 * User Entity Model representing system staff and administrators.
 * Used for role-based access control, secure authentication, and password recovery.
 */
public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role; // Admin, Receptionist, Staff
    private String securityQuestion;
    private String securityAnswer;
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Parameterized constructor for user authentication and creation.
     *
     * @param username     Unique account username
     * @param passwordHash SHA-256 hashed password
     * @param fullName     Full legal or display name of the user
     * @param email        Contact email address
     * @param role         System permission role (Admin, Receptionist, Staff)
     */
    public User(String username, String passwordHash, String fullName, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
