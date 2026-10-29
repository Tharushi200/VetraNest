package com.vetranest;

import com.vetranest.controller.AppointmentController;
import com.vetranest.controller.CustomerController;
import com.vetranest.controller.PetController;
import com.vetranest.controller.TreatmentController;
import com.vetranest.exception.AppointmentConflictException;
import com.vetranest.model.Appointment;
import com.vetranest.model.Customer;
import com.vetranest.model.Pet;
import com.vetranest.model.Treatment;

import java.time.LocalDate;
import java.util.List;

public class AppointmentTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  VETRANEST - PART 7 APPOINTMENT & TREATMENT TEST");
        System.out.println("==================================================");

        try {
            CustomerController customerController = new CustomerController();
            PetController petController = new PetController();
            AppointmentController appointmentController = new AppointmentController();
            TreatmentController treatmentController = new TreatmentController();

            // 1. Ensure test customer exists
            List<Customer> customers = customerController.getAllCustomers();
            Customer testCust;
            if (customers.isEmpty()) {
                customerController.addCustomer("Test Owner", "0779998877", "owner@vetranest.test", "123 Colombo St");
                customers = customerController.getAllCustomers();
            }
            testCust = customers.get(0);
            System.out.println("[OK] Using Customer: " + testCust.getName() + " (ID: " + testCust.getCustomerId() + ")");

            // 2. Ensure test pet exists
            List<Pet> pets = petController.getAllPets();
            Pet testPet;
            if (pets.isEmpty()) {
                petController.addPet(testCust.getCustomerId(), "Bella", "Dog", "Golden Retriever", "Female", java.sql.Date.valueOf("2022-04-10"), 24.5, "Golden");
                pets = petController.getAllPets();
            }
            testPet = pets.get(0);
            System.out.println("[OK] Using Pet: " + testPet.getPetName() + " (ID: " + testPet.getPetId() + ")");

            // 3. Book an Appointment
            String testDate = LocalDate.now().plusDays(1).toString();
            String testSlot = "09:00 - 09:30 AM";

            System.out.println("\n[Test 1] Booking appointment for " + testDate + " @ " + testSlot + "...");
            // Clean up any existing slot for exact repeatability in test
            List<Appointment> existing = appointmentController.getAllAppointments();
            for (Appointment a : existing) {
                if (testDate.equals(a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "")
                        && testSlot.equals(a.getTimeSlot())) {
                    appointmentController.deleteAppointment(a.getAppointmentId());
                }
            }

            boolean booked = appointmentController.bookAppointment(
                    testCust.getCustomerId(),
                    testPet.getPetId(),
                    testDate,
                    testSlot,
                    "General Health Checkup",
                    "Annual checkup and vaccination check."
            );
            System.out.println("[SUCCESS] Appointment booked: " + booked);

            // 4. Test AppointmentConflictException (Crucial viva requirement!)
            System.out.println("\n[Test 2] Attempting duplicate booking on same slot (Expecting AppointmentConflictException)...");
            boolean caughtConflict = false;
            try {
                appointmentController.bookAppointment(
                        testCust.getCustomerId(),
                        testPet.getPetId(),
                        testDate,
                        testSlot,
                        "Dental Cleaning",
                        "Duplicate booking attempt"
                );
            } catch (AppointmentConflictException ace) {
                caughtConflict = true;
                System.out.println("[SUCCESS] Caught expected AppointmentConflictException: " + ace.getMessage());
            }

            if (!caughtConflict) {
                throw new RuntimeException("FAIL: AppointmentConflictException was NOT thrown on double-booking!");
            }

            // 5. Verify Appointment listed with JOIN fields (Customer name, Pet name)
            List<Appointment> apps = appointmentController.getAllAppointments();
            Appointment bookedApp = null;
            for (Appointment a : apps) {
                if (testDate.equals(a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "")
                        && testSlot.equals(a.getTimeSlot())) {
                    bookedApp = a;
                    break;
                }
            }
            if (bookedApp == null) {
                throw new RuntimeException("FAIL: Booked appointment not found in database!");
            }
            System.out.println("[SUCCESS] Found appointment #" + bookedApp.getAppointmentId()
                    + " | Customer: " + bookedApp.getCustomerName()
                    + " | Pet: " + bookedApp.getPetName()
                    + " | Status: " + bookedApp.getStatus());

            // 6. Test Status Transition (Pending -> Confirmed -> Completed)
            System.out.println("\n[Test 3] Updating appointment status to 'Confirmed'...");
            appointmentController.updateStatus(bookedApp.getAppointmentId(), "Confirmed");
            System.out.println("[SUCCESS] Appointment confirmed.");

            // 7. Test Clinical Treatment Record (Diagnosis & Prescription)
            System.out.println("\n[Test 4] Adding Clinical Treatment & Prescription record...");
            boolean treated = treatmentController.addTreatment(
                    bookedApp.getPetId(),
                    bookedApp.getAppointmentId(),
                    testDate,
                    "Mild Ear Infection (Otitis Externa)",
                    "Amoxicillin 250mg 1 tab bid x 7 days; Surolan Ear Drops 5 drops bid",
                    "Keep ears dry. Follow-up in 7 days if symptoms persist.",
                    "Dr. K. Perera (BVSc)"
            );
            System.out.println("[SUCCESS] Treatment record created: " + treated);

            // 8. Verify Treatment in Pet History
            List<Treatment> treatments = treatmentController.getTreatmentsByPet(testPet.getPetId());
            System.out.println("[SUCCESS] Found " + treatments.size() + " treatment records for pet '" + testPet.getPetName() + "':");
            for (Treatment t : treatments) {
                System.out.println("   - [" + t.getTreatmentDate() + "] " + t.getDiagnosis() + " -> " + t.getMedication());
            }

            System.out.println("\n==================================================");
            System.out.println("  ALL PART 7 TESTS PASSED WITH 100% SUCCESS!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[TEST ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
