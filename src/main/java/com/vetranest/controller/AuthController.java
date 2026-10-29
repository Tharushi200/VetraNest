package com.vetranest.controller;

import com.vetranest.dao.UserDAO;
import com.vetranest.exception.DuplicateUserException;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.User;
import com.vetranest.util.InputValidator;
import com.vetranest.util.PasswordHasher;

import java.sql.SQLException;

/**
 * Controller managing Authentication, User Registration and Password Recovery.
 * MVC Pattern: Handles business logic between Views and DAOs.
 */
public class AuthController {

    private final UserDAO userDAO;
    private static User currentUser;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    /**
     * Authenticates user credentials.
     */
    public User login(String username, String password) throws ValidationException, SQLException {
        InputValidator.validateNotEmpty(username, "Username");
        InputValidator.validateNotEmpty(password, "Password");

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            return user;
        }
        return null;
    }

    /**
     * Registers a new staff or admin user.
     * Enforces input validations and handles DuplicateUserException.
     */
    public boolean register(String fullName, String username, String email, String role,
                            String password, String confirmPassword, String securityAnswer)
            throws ValidationException, DuplicateUserException, SQLException {

        // Validate all inputs
        InputValidator.validateNotEmpty(fullName, "Full Name");
        InputValidator.validateUsername(username);
        InputValidator.validateEmail(email);
        InputValidator.validatePassword(password);
        InputValidator.validateNotEmpty(securityAnswer, "Security Answer");

        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Confirm Password", "Passwords do not match.");
        }

        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim());
        newUser.setRole(role != null ? role : "Admin");
        newUser.setPasswordHash(PasswordHasher.hash(password));
        newUser.setSecurityAnswer(securityAnswer.trim());

        return userDAO.create(newUser);
    }

    /**
     * Resets password using security answer validation.
     */
    public boolean resetPassword(String username, String securityAnswer, String newPassword, String confirmPassword)
            throws ValidationException, SQLException {

        InputValidator.validateNotEmpty(username, "Username");
        InputValidator.validateNotEmpty(securityAnswer, "Security Answer");
        InputValidator.validatePassword(newPassword);

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Confirm Password", "Passwords do not match.");
        }

        return userDAO.resetPassword(username, securityAnswer, newPassword);
    }
}
