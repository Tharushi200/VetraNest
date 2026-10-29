package com.vetranest.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Treatment Entity Model representing clinical diagnoses, medications, and veterinary prescriptions.
 * Tracks medical histories linked to patients and consultation appointments.
 */
public class Treatment {

    private int treatmentId;
    private int appointmentId;
    private int petId;
    private String petName;        // Joined from pets table
    private String petSpecies;     // Joined from pets table
    private String ownerName;      // Joined from customers table
    private Date treatmentDate;
    private String diagnosis;
    private String medication;
    private String instructions;
    private String doctorName;
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public Treatment() {
    }

    /**
     * Parameterized constructor for clinical treatment records.
     *
     * @param appointmentId Associated appointment ID (0 if walk-in)
     * @param petId         Patient pet ID
     * @param treatmentDate Date the treatment was administered
     * @param diagnosis     Clinical diagnosis findings
     * @param medication    Prescribed drugs and dosage
     * @param instructions  Care guidelines and diet instructions
     * @param doctorName    Attending veterinary surgeon name
     */
    public Treatment(int appointmentId, int petId, Date treatmentDate,
                     String diagnosis, String medication, String instructions, String doctorName) {
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.medication = medication;
        this.instructions = instructions;
        this.doctorName = doctorName;
    }

    public int getTreatmentId() { 
        return treatmentId; 
    }
    
    public void setTreatmentId(int treatmentId) { 
        this.treatmentId = treatmentId; 
    }

    public int getAppointmentId() { 
        return appointmentId; 
    }
    
    public void setAppointmentId(int appointmentId) { 
        this.appointmentId = appointmentId; 
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

    public String getPetSpecies() { 
        return petSpecies; 
    }
    
    public void setPetSpecies(String petSpecies) { 
        this.petSpecies = petSpecies; 
    }

    public String getOwnerName() { 
        return ownerName; 
    }
    
    public void setOwnerName(String ownerName) { 
        this.ownerName = ownerName; 
    }

    public Date getTreatmentDate() { 
        return treatmentDate; 
    }
    
    public void setTreatmentDate(Date treatmentDate) { 
        this.treatmentDate = treatmentDate; 
    }

    public String getDiagnosis() { 
        return diagnosis; 
    }
    
    public void setDiagnosis(String diagnosis) { 
        this.diagnosis = diagnosis; 
    }

    public String getMedication() { 
        return medication; 
    }
    
    public void setMedication(String medication) { 
        this.medication = medication; 
    }

    public String getInstructions() { 
        return instructions; 
    }
    
    public void setInstructions(String instructions) { 
        this.instructions = instructions; 
    }

    public String getDoctorName() { 
        return doctorName; 
    }
    
    public void setDoctorName(String doctorName) { 
        this.doctorName = doctorName; 
    }

    public Timestamp getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Timestamp createdAt) { 
        this.createdAt = createdAt; 
    }
}
