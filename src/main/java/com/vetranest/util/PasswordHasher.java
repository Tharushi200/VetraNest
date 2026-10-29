package com.vetranest.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for hashing and verifying passwords using SHA-256.
 * Adheres to enterprise security standards.
 */
public class PasswordHasher {

    /**
     * Computes a cryptographic SHA-256 hash of a plain text password.
     *
     * @param rawPassword The raw, plain-text password
     * @return Hexadecimal-encoded SHA-256 hash string, or null if input is null
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a raw password matches a stored SHA-256 hash.
     *
     * @param rawPassword The raw password attempt
     * @param storedHash  The stored hexadecimal hash from the database
     * @return true if matches, false otherwise
     */
    public static boolean verify(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) return false;
        String hashedRaw = hash(rawPassword);
        return hashedRaw.equalsIgnoreCase(storedHash);
    }
}
