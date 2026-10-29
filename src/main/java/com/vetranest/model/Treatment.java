package com.vetranest.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Treatment Entity Model - Clinical diagnosis and prescription records.
 */
public class Treatment {

    private int treatmentId;
    private int appointmentId;
    private int petId;
    private String petName;        // Joined
    private String petSpecies;     // Joined
    private String ownerName;      // Joined
    private Date treatmentDate;
    private String diagnosis;
    private String medication;
    private String instructions;
    private String doctorName;
    private Timestamp createdAt;

    public Treatment() {
    }

    // Getters and Setters
    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPetSpecies() { return petSpecies; }
    public void setPetSpecies(String petSpecies) { this.petSpecies = petSpecies; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Date getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(Date treatmentDate) { this.treatmentDate = treatmentDate; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
