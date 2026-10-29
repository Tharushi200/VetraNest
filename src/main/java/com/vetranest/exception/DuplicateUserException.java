package com.vetranest.exception;

/**
 * User-defined Exception thrown when an attempt is made to register
 * or create an account with a username that already exists in the system.
 *
 * Fulfills NIBM EAD 1 Coursework Rubric:
 * "Implement input validation and exception handling, including at least one
 * user-defined exception where applicable." (10 Marks)
 */
public class DuplicateUserException extends Exception {

    private final String attemptedUsername;

    public DuplicateUserException(String username) {
        super("The username '" + username + "' is already registered in VetraNest. Please choose a different username.");
        this.attemptedUsername = username;
    }

    public DuplicateUserException(String username, String message) {
        super(message);
        this.attemptedUsername = username;
    }

    public String getAttemptedUsername() {
        return attemptedUsername;
    }
}
