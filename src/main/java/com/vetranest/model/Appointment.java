package com.vetranest.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Appointment Entity Model representing scheduled veterinary consultations and visits.
 * Holds transactional appointment data along with joined customer and pet details.
 */
public class Appointment {

    private int appointmentId;
    private String appointmentCode;
    private int customerId;
    private String customerName;   // Joined from customers table
    private int petId;
    private String petName;        // Joined from pets table
    private Date appointmentDate;
    private String timeSlot;
    private String serviceType;    // Vaccination, Checkup, Grooming, Treatment, Surgery
    private String status;         // Pending, Confirmed, Completed, Cancelled
    private String notes;
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public Appointment() {
    }

    /**
     * Parameterized constructor for creating an appointment instance.
     *
     * @param appointmentCode Unique appointment identifier code (e.g. A001)
     * @param customerId      Owner customer ID
     * @param petId           Patient pet ID
     * @param appointmentDate Scheduled consultation date
     * @param timeSlot        Scheduled time slot
     * @param serviceType     Type of clinical or care service
     * @param status          Booking status
     * @param notes           Special instructions or clinical remarks
     */
    public Appointment(String appointmentCode, int customerId, int petId,
                       Date appointmentDate, String timeSlot, String serviceType,
                       String status, String notes) {
        this.appointmentCode = appointmentCode;
        this.customerId = customerId;
        this.petId = petId;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.serviceType = serviceType;
        this.status = status;
        this.notes = notes;
    }

    public int getAppointmentId() { 
        return appointmentId; 
    }
    
    public void setAppointmentId(int appointmentId) { 
        this.appointmentId = appointmentId; 
    }

    public String getAppointmentCode() { 
        return appointmentCode; 
    }
    
    public void setAppointmentCode(String appointmentCode) { 
        this.appointmentCode = appointmentCode; 
    }

    public int getCustomerId() { 
        return customerId; 
    }
    
    public void setCustomerId(int customerId) { 
        this.customerId = customerId; 
    }

    public String getCustomerName() { 
        return customerName; 
    }
    
    public void setCustomerName(String customerName) { 
        this.customerName = customerName; 
    }

    public int getPetId() { 
        return petId; 
    }
    
    public void setPetId(int petId) { 
        this.petId = petId; 
    }

    public String getPetName() { 
        return petName; 
    }
    
    public void setPetName(String petName) { 
        this.petName = petName; 
    }

    public Date getAppointmentDate() { 
        return appointmentDate; 
    }
    
    public void setAppointmentDate(Date appointmentDate) { 
        this.appointmentDate = appointmentDate; 
    }

    public String getTimeSlot() { 
        return timeSlot; 
    }
    
    public void setTimeSlot(String timeSlot) { 
        this.timeSlot = timeSlot; 
    }

    public String getServiceType() { 
        return serviceType; 
    }
    
    public void setServiceType(String serviceType) { 
        this.serviceType = serviceType; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }

    public Timestamp getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Timestamp createdAt) { 
        this.createdAt = createdAt; 
    }

    @Override
    public String toString() {
        return appointmentCode + " - " + petName + " (" + serviceType + ")";
    }
}
