package com.vetranest;

import com.vetranest.service.ReportService;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.File;

public class ReportTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  VETRANEST - PART 8 JASPERREPORTS MODULE TEST");
        System.out.println("==================================================");

        try {
            ReportService service = ReportService.getInstance();
            File outputDir = new File("target/reports_output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Pre-compile JRXML files to .jasper in resources
            System.out.println("[Compiling] Compiling JRXML to .jasper files...");
            net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile(
                    "src/main/resources/reports/comprehensive_visit_report.jrxml",
                    "src/main/resources/reports/comprehensive_visit_report.jasper");
            net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile(
                    "src/main/resources/reports/customer_pet_summary.jrxml",
                    "src/main/resources/reports/customer_pet_summary.jasper");
            net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile(
                    "src/main/resources/reports/pet_history_report.jrxml",
                    "src/main/resources/reports/pet_history_report.jasper");
            System.out.println("[SUCCESS] Pre-compiled all 3 reports to .jasper!");

            // Test 1: Comprehensive 4-Table Visit Report
            System.out.println("\n[Test 1] Generating Comprehensive 4-Table Visit Report...");
            JasperPrint print1 = service.generateComprehensiveVisitReport();
            System.out.println("[SUCCESS] Report filled. Page count: " + print1.getPages().size());

            File pdf1 = new File(outputDir, "Test_Comprehensive_Visits.pdf");
            service.exportToPdf(print1, pdf1);
            if (pdf1.exists() && pdf1.length() > 0) {
                System.out.println("[SUCCESS] Exported PDF: " + pdf1.getAbsolutePath() + " (" + pdf1.length() + " bytes)");
            } else {
                throw new RuntimeException("FAIL: PDF not exported or empty!");
            }

            // Test 2: Pet Clinical Medical History Report (All pets)
            System.out.println("\n[Test 2] Generating Pet Clinical Medical History Report (All pets)...");
            JasperPrint print2 = service.generatePetHistoryReport(0);
            System.out.println("[SUCCESS] Report filled. Page count: " + print2.getPages().size());

            File pdf2 = new File(outputDir, "Test_Pet_Medical_History.pdf");
            service.exportToPdf(print2, pdf2);
            if (pdf2.exists() && pdf2.length() > 0) {
                System.out.println("[SUCCESS] Exported PDF: " + pdf2.getAbsolutePath() + " (" + pdf2.length() + " bytes)");
            } else {
                throw new RuntimeException("FAIL: Pet History PDF not exported or empty!");
            }

            // Test 3: Customer & Patient Directory Report
            System.out.println("\n[Test 3] Generating Client & Patient Directory Report...");
            JasperPrint print3 = service.generateCustomerPetSummaryReport();
            System.out.println("[SUCCESS] Report filled. Page count: " + print3.getPages().size());

            File pdf3 = new File(outputDir, "Test_Client_Patient_Directory.pdf");
            service.exportToPdf(print3, pdf3);
            if (pdf3.exists() && pdf3.length() > 0) {
                System.out.println("[SUCCESS] Exported PDF: " + pdf3.getAbsolutePath() + " (" + pdf3.length() + " bytes)");
            } else {
                throw new RuntimeException("FAIL: Directory PDF not exported or empty!");
            }

            System.out.println("\n==================================================");
            System.out.println("  ALL PART 8 JASPERREPORTS TESTS PASSED 100%!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("[TEST ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
