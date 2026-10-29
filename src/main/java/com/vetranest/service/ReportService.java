package com.vetranest.service;

import com.vetranest.config.DatabaseConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Report Service for compiling, filling, viewing, and exporting JasperReports.
 * Prioritizes pre-compiled .jasper files for instantaneous loading with zero compiler dependencies,
 * falling back to .jrxml compilation if needed.
 */
public class ReportService {

    private static ReportService instance;
    private final Map<String, JasperReport> compiledReportsCache = new ConcurrentHashMap<>();

    private ReportService() {
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    /**
     * Loads, fills, and produces a JasperPrint object using active DB connection.
     * Uses precompiled .jasper files whenever available.
     */
    public JasperPrint generateReport(String baseReportName, Map<String, Object> parameters) throws Exception {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // 1. Try pre-compiled .jasper binary resource (fastest: ~0.02s, no JDK compiler required)
        String jasperResource = "reports/" + baseReportName + ".jasper";
        InputStream jasperStream = getClass().getClassLoader().getResourceAsStream(jasperResource);
        if (jasperStream != null) {
            try {
                return JasperFillManager.fillReport(jasperStream, parameters, conn);
            } finally {
                try { jasperStream.close(); } catch (Exception ignored) {}
            }
        }

        // 2. Try file system for .jasper
        File jasperFile = new File("src/main/resources/" + jasperResource);
        if (jasperFile.exists()) {
            try (InputStream fis = new java.io.FileInputStream(jasperFile)) {
                return JasperFillManager.fillReport(fis, parameters, conn);
            }
        }

        // 3. Check memory cache for previously compiled template
        JasperReport cachedReport = compiledReportsCache.get(baseReportName);
        if (cachedReport != null) {
            return JasperFillManager.fillReport(cachedReport, parameters, conn);
        }

        // 4. Fallback: compile from .jrxml template
        String jrxmlResource = "reports/" + baseReportName + ".jrxml";
        InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream(jrxmlResource);
        if (jrxmlStream == null) {
            File jrxmlFile = new File("src/main/resources/" + jrxmlResource);
            if (jrxmlFile.exists()) {
                jrxmlStream = new java.io.FileInputStream(jrxmlFile);
            } else {
                throw new IllegalArgumentException("Report template not found: " + jrxmlResource);
            }
        }

        try {
            JasperReport compiled = JasperCompileManager.compileReport(jrxmlStream);
            compiledReportsCache.put(baseReportName, compiled);
            return JasperFillManager.fillReport(compiled, parameters, conn);
        } finally {
            try { jrxmlStream.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * Generates Customer & Pet Directory Summary Report.
     */
    public JasperPrint generateCustomerPetSummaryReport() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("CLINIC_NAME", "VetraNest Animal Hospital");
        return generateReport("customer_pet_summary", params);
    }

    /**
     * Generates Pet Clinical Medical History Report (filtered by petId if > 0).
     */
    public JasperPrint generatePetHistoryReport(int petId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("PET_ID", petId);
        params.put("CLINIC_NAME", "VetraNest Animal Hospital");
        return generateReport("pet_history_report", params);
    }

    /**
     * Generates Appointments & Visits Audit Report (4-Table relational join).
     */
    public JasperPrint generateComprehensiveVisitReport() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("CLINIC_NAME", "VetraNest Animal Hospital & Pet Care");
        return generateReport("comprehensive_visit_report", params);
    }

    /**
     * Displays the generated report in an interactive JasperViewer window.
     * Brings window directly to the front and sets clean dimensions.
     */
    public void viewReport(String windowTitle, JasperPrint jasperPrint) {
        SwingUtilities.invokeLater(() -> {
            try {
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle(windowTitle != null ? windowTitle : "VetraNest Management Report");
                viewer.setSize(980, 720);
                viewer.setLocationRelativeTo(null);
                viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                viewer.setVisible(true);
                viewer.toFront();
                viewer.requestFocus();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Exports the filled report directly to a PDF document.
     */
    public void exportToPdf(JasperPrint jasperPrint, File outputFile) throws JRException {
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.getAbsolutePath());
    }
}
