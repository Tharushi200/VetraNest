package com.vetranest.util;

import com.vetranest.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Validates system inputs against enterprise business rules.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^(?:0|\\+94)?[0-9]{9,10}$");

    private static final Pattern USERNAME_PATTERN = 
            Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty.");
        }
    }

    public static void validateUsername(String username) throws ValidationException {
        validateNotEmpty(username, "Username");
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new ValidationException("Username", "Username must be 3-20 characters long (letters, numbers, underscore only).");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Email", "Please enter a valid email address (e.g., user@example.com).");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        validateNotEmpty(phone, "Phone Number");
        String sanitized = phone.replaceAll("\\s+", "");
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new ValidationException("Phone Number", "Please enter a valid phone number (e.g., 0771234567).");
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password", "Password must be at least 6 characters long.");
        }
    }
}
