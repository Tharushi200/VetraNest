package com.vetranest.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Appointment Entity Model - Transaction record for clinic visits.
 */
public class Appointment {

    private int appointmentId;
    private String appointmentCode;
    private int customerId;
    private String customerName;   // Joined
    private int petId;
    private String petName;        // Joined
    private Date appointmentDate;
    private String timeSlot;
    private String serviceType;   // Vaccination, Checkup, Grooming, Treatment, Surgery
    private String status;        // Pending, Confirmed, Completed, Cancelled
    private String notes;
    private Timestamp createdAt;

    public Appointment() {
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return appointmentCode + " - " + petName + " (" + serviceType + ")";
    }
}
