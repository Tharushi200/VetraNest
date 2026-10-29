package com.vetranest;

import com.vetranest.controller.AuthController;
import com.vetranest.exception.DuplicateUserException;
import com.vetranest.model.User;

public class AuthTest {

    public static void main(String[] args) {
        System.out.println("--- Running Authentication & Exception Tests ---");
        AuthController auth = new AuthController();

        // Test 1: Successful Login
        try {
            User user = auth.login("admin", "admin123");
            if (user != null) {
                System.out.println("✓ Test 1 Passed: Login successful for " + user.getFullName());
            } else {
                System.err.println("✗ Test 1 Failed: Login returned null.");
            }
        } catch (Exception e) {
            System.err.println("✗ Test 1 Failed with exception: " + e.getMessage());
        }

        // Test 2: DuplicateUserException Trigger
        try {
            System.out.println("Attempting to register existing username 'admin'...");
            auth.register("Duplicate Admin", "admin", "dup@vetranest.lk", "Admin", "admin123", "admin123", "VN2026");
            System.err.println("✗ Test 2 Failed: Did NOT throw DuplicateUserException!");
        } catch (DuplicateUserException ex) {
            System.out.println("✓ Test 2 Passed: Successfully caught User-Defined Exception: " + ex.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Test 2 Failed with unexpected exception: " + e.getMessage());
        }

        System.out.println("--- All Auth & Exception Unit Tests Complete! ---");
    }
}
