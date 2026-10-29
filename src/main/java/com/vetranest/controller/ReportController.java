package com.vetranest.controller;

import com.vetranest.dao.AppointmentDAO;
import com.vetranest.dao.CustomerDAO;
import com.vetranest.dao.PetDAO;
import com.vetranest.dao.TreatmentDAO;
import com.vetranest.model.Pet;
import com.vetranest.service.ReportService;
import net.sf.jasperreports.engine.JasperPrint;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller mediating report generation, parameter extraction, and file export.
 * Executes generation asynchronously with cursor indicators and direct PDF launching.
 */
public class ReportController {

    private final ReportService reportService;
    private final PetDAO petDAO;
    private final CustomerDAO customerDAO;
    private final AppointmentDAO appointmentDAO;
    private final TreatmentDAO treatmentDAO;

    public ReportController() {
        this.reportService = ReportService.getInstance();
        this.petDAO = new PetDAO();
        this.customerDAO = new CustomerDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.treatmentDAO = new TreatmentDAO();
    }

    public List<Pet> getPetsForFilter() throws SQLException {
        return petDAO.getAllPets();
    }

    public int getCustomerCount() {
        try {
            return customerDAO.getAllCustomers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPetCount() {
        try {
            return petDAO.getAllPets().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getAppointmentCount() {
        try {
            return appointmentDAO.getAllAppointments().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTreatmentCount() {
        try {
            return treatmentDAO.getAllTreatments().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Previews Customer & Pet Directory Report.
     */
    public void previewCustomerPetSummary(Component parent) {
        runAsync(parent, () -> {
            JasperPrint print = reportService.generateCustomerPetSummaryReport();
            reportService.viewReport("Customer Directory Report", print);
        });
    }

    /**
     * Exports Customer & Pet Directory Report to PDF.
     */
    public void exportCustomerPetSummary(Component parent, File destFile) {
        runAsync(parent, () -> {
            JasperPrint print = reportService.generateCustomerPetSummaryReport();
            reportService.exportToPdf(print, destFile);
            showExportSuccess(parent, destFile);
        });
    }

    /**
     * Previews Pet History Report for all or specific pet.
     */
    public void previewPetHistoryReport(Component parent, int petId, String petName) {
        String title = petId > 0 ? "Pet Medical History - " + petName : "All Pets Medical History";
        runAsync(parent, () -> {
            JasperPrint print = reportService.generatePetHistoryReport(petId);
            reportService.viewReport(title, print);
        });
    }

    /**
     * Exports Pet History Report to PDF.
     */
    public void exportPetHistoryReport(Component parent, int petId, File destFile) {
        runAsync(parent, () -> {
            JasperPrint print = reportService.generatePetHistoryReport(petId);
            reportService.exportToPdf(print, destFile);
            showExportSuccess(parent, destFile);
        });
    }

    /**
     * Previews Appointments & Visits Summary Report.
     */
    public void previewComprehensiveVisitReport(Component parent) {
        runAsync(parent, () -> {
            JasperPrint print = reportService.generateComprehensiveVisitReport();
            reportService.viewReport("Appointments & Visits Summary", print);
        });
    }

    /**
     * Exports Appointments Summary Report to PDF.
     */
    public void exportComprehensiveVisitReport(Component parent, File destFile) {
        runAsync(parent, () -> {
            JasperPrint print = reportService.generateComprehensiveVisitReport();
            reportService.exportToPdf(print, destFile);
            showExportSuccess(parent, destFile);
        });
    }

    private void runAsync(Component parent, ReportTask task) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        if (window != null) {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.execute();
                return null;
            }

            @Override
            protected void done() {
                if (window != null) {
                    window.setCursor(Cursor.getDefaultCursor());
                }
                try {
                    get();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    cause.printStackTrace();
                    JOptionPane.showMessageDialog(parent,
                            "Report generation failed:\n" + cause.getMessage(),
                            "Report Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void showExportSuccess(Component parent, File file) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(
                    parent,
                    "Report exported successfully!\n\nFile saved to:\n" + file.getAbsolutePath() + "\n\nDo you want to open the PDF file now?",
                    "Export Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    if (file.exists() && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Could not open PDF: " + ex.getMessage());
                }
            }
        });
    }

    @FunctionalInterface
    private interface ReportTask {
        void execute() throws Exception;
    }
}
