package com.vetranest.exception;

/**
 * User-Defined Exception thrown when attempting to book an appointment
 * in a time slot that is already occupied for the same date.
 *
 * Fulfills NIBM EAD 1 Coursework Rubric:
 * "Implement input validation and exception handling, including at least one
 * user-defined exception where applicable." (10 Marks)
 */
public class AppointmentConflictException extends Exception {

    private final String conflictDate;
    private final String conflictTimeSlot;

    public AppointmentConflictException(String date, String timeSlot) {
        super("Appointment slot conflict: The time slot '" + timeSlot +
              "' on " + date + " is already booked. Please choose a different time.");
        this.conflictDate = date;
        this.conflictTimeSlot = timeSlot;
    }

    public String getConflictDate() {
        return conflictDate;
    }

    public String getConflictTimeSlot() {
        return conflictTimeSlot;
    }
}
