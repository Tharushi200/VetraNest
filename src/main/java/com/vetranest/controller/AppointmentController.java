package com.vetranest.controller;

import com.vetranest.dao.AppointmentDAO;
import com.vetranest.dao.CustomerDAO;
import com.vetranest.dao.PetDAO;
import com.vetranest.exception.AppointmentConflictException;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Appointment;
import com.vetranest.model.Customer;
import com.vetranest.model.Pet;
import com.vetranest.util.InputValidator;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller managing Appointment booking, conflict prevention, and status transitions.
 * Implements AppointmentConflictException for double-booking prevention.
 */
public class AppointmentController {

    private final AppointmentDAO appointmentDAO;
    private final PetDAO petDAO;
    private final CustomerDAO customerDAO;

    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
        this.petDAO = new PetDAO();
        this.customerDAO = new CustomerDAO();
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        return appointmentDAO.getAllAppointments();
    }

    public List<Appointment> searchAppointments(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return appointmentDAO.getAllAppointments();
        }
        return appointmentDAO.searchAppointments(keyword);
    }

    public List<Pet> getAllPets() throws SQLException {
        return petDAO.getAllPets();
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    /**
     * Books a new appointment - checks for slot conflict via AppointmentConflictException.
     */
    public boolean bookAppointment(int customerId, int petId, String dateStr,
                                   String timeSlot, String serviceType, String notes)
            throws ValidationException, AppointmentConflictException, SQLException {

        // Validate inputs
        if (customerId <= 0) throw new ValidationException("Owner", "Please select a valid customer.");
        if (petId <= 0) throw new ValidationException("Pet", "Please select a valid pet.");
        InputValidator.validateNotEmpty(dateStr, "Appointment Date");
        InputValidator.validateNotEmpty(timeSlot, "Time Slot");
        InputValidator.validateNotEmpty(serviceType, "Service Type");

        Date appDate;
        try {
            appDate = Date.valueOf(dateStr.trim());
        } catch (Exception e) {
            throw new ValidationException("Appointment Date", "Invalid date format. Use YYYY-MM-DD.");
        }

        // === AppointmentConflictException: Check if slot already booked ===
        boolean slotTaken = appointmentDAO.isSlotBooked(appDate, timeSlot, 0);
        if (slotTaken) {
            throw new AppointmentConflictException(dateStr, timeSlot);
        }

        Appointment a = new Appointment();
        a.setCustomerId(customerId);
        a.setPetId(petId);
        a.setAppointmentDate(appDate);
        a.setTimeSlot(timeSlot);
        a.setServiceType(serviceType);
        a.setStatus("Pending");
        a.setNotes(notes != null ? notes.trim() : "");

        return appointmentDAO.insert(a);
    }

    /**
     * Updates appointment - also checks for slot conflict on reschedule.
     */
    public boolean updateAppointment(int appointmentId, int customerId, int petId,
                                     String dateStr, String timeSlot,
                                     String serviceType, String status, String notes)
            throws ValidationException, AppointmentConflictException, SQLException {

        if (customerId <= 0) throw new ValidationException("Owner", "Please select a valid customer.");
        if (petId <= 0) throw new ValidationException("Pet", "Please select a valid pet.");
        InputValidator.validateNotEmpty(dateStr, "Appointment Date");
        InputValidator.validateNotEmpty(timeSlot, "Time Slot");

        Date appDate;
        try {
            appDate = Date.valueOf(dateStr.trim());
        } catch (Exception e) {
            throw new ValidationException("Appointment Date", "Invalid date format. Use YYYY-MM-DD.");
        }

        // Check conflict excluding current appointment
        boolean slotTaken = appointmentDAO.isSlotBooked(appDate, timeSlot, appointmentId);
        if (slotTaken) {
            throw new AppointmentConflictException(dateStr, timeSlot);
        }

        Appointment a = new Appointment();
        a.setAppointmentId(appointmentId);
        a.setCustomerId(customerId);
        a.setPetId(petId);
        a.setAppointmentDate(appDate);
        a.setTimeSlot(timeSlot);
        a.setServiceType(serviceType);
        a.setStatus(status != null ? status : "Pending");
        a.setNotes(notes != null ? notes.trim() : "");

        return appointmentDAO.update(a);
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        return appointmentDAO.updateStatus(id, status);
    }

    public boolean deleteAppointment(int id) throws SQLException {
        return appointmentDAO.delete(id);
    }

    public String[] getAvailableTimeSlots() {
        return new String[]{
                "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM",
                "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "01:00 PM", "01:30 PM", "02:00 PM",
                "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM",
                "04:30 PM", "05:00 PM"
        };
    }
}
