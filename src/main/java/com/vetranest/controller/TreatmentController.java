package com.vetranest.controller;

import com.vetranest.dao.PetDAO;
import com.vetranest.dao.TreatmentDAO;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Pet;
import com.vetranest.model.Treatment;
import com.vetranest.util.InputValidator;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller managing Treatment/Prescription records and pet history.
 */
public class TreatmentController {

    private final TreatmentDAO treatmentDAO;
    private final PetDAO petDAO;

    public TreatmentController() {
        this.treatmentDAO = new TreatmentDAO();
        this.petDAO = new PetDAO();
    }

    public List<Treatment> getAllTreatments() throws SQLException {
        return treatmentDAO.getAllTreatments();
    }

    public List<Treatment> getTreatmentsByPet(int petId) throws SQLException {
        return treatmentDAO.getTreatmentsByPet(petId);
    }

    public List<Pet> getAllPets() throws SQLException {
        return petDAO.getAllPets();
    }

    public boolean addTreatment(int petId, int appointmentId, String dateStr,
                                String diagnosis, String medication, String instructions, String doctor)
            throws ValidationException, SQLException {
        return addTreatment(petId, appointmentId, dateStr, diagnosis, medication, instructions, doctor, 0.0);
    }

    public boolean addTreatment(int petId, int appointmentId, String dateStr,
                                String diagnosis, String medication, String instructions, String doctor, double cost)
            throws ValidationException, SQLException {

        if (petId <= 0) throw new ValidationException("Pet", "Please select a valid pet.");
        InputValidator.validateNotEmpty(diagnosis, "Diagnosis");
        InputValidator.validateNotEmpty(medication, "Medication");

        Treatment t = new Treatment();
        t.setPetId(petId);
        t.setAppointmentId(appointmentId);
        t.setDiagnosis(diagnosis.trim());
        t.setMedication(medication.trim());
        t.setInstructions(instructions != null ? instructions.trim() : "");
        t.setDoctorName(doctor != null && !doctor.trim().isEmpty() ? doctor.trim() : "Dr. K. Perera");

        try {
            t.setTreatmentDate(dateStr != null && !dateStr.trim().isEmpty() ?
                    Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new ValidationException("Treatment Date", "Invalid date format. Use YYYY-MM-DD.");
        }

        return treatmentDAO.insert(t, cost);
    }

    public boolean updateTreatment(int treatmentId, String dateStr, String diagnosis,
                                   String medication, String instructions, String doctor)
            throws ValidationException, SQLException {

        InputValidator.validateNotEmpty(diagnosis, "Diagnosis");
        InputValidator.validateNotEmpty(medication, "Medication");

        Treatment t = new Treatment();
        t.setTreatmentId(treatmentId);
        t.setDiagnosis(diagnosis.trim());
        t.setMedication(medication.trim());
        t.setInstructions(instructions != null ? instructions.trim() : "");
        t.setDoctorName(doctor != null && !doctor.trim().isEmpty() ? doctor.trim() : "Dr. K. Perera");

        try {
            t.setTreatmentDate(dateStr != null && !dateStr.trim().isEmpty() ?
                    Date.valueOf(dateStr.trim()) : new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new ValidationException("Treatment Date", "Invalid date format. Use YYYY-MM-DD.");
        }

        return treatmentDAO.update(t);
    }

    public boolean deleteTreatment(int id) throws SQLException {
        return treatmentDAO.delete(id);
    }
}
