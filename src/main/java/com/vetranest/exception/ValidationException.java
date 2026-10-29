package com.vetranest.exception;

/**
 * User-defined Exception thrown when business input validation fails.
 */
public class ValidationException extends Exception {

    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
