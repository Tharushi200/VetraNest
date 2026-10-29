package com.vetranest.view.report;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.ReportController;
import com.vetranest.model.Pet;
import com.vetranest.util.ImageUtil;
import com.vetranest.view.components.ModernCard;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Management Reports View Panel.
 * Features 3D illustration cards, concise content, and precompiled JasperReports.
 */
public class ReportPanel extends JPanel {

    private final ReportController controller;
    private JComboBox<PetOption> cmbPetFilter;

    public ReportPanel() {
        this.controller = new ReportController();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_APP);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Header Section (Clean & Minimal)
        JPanel headerPanel = new JPanel(new BorderLayout(0, 2));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Management Reports");
        titleLabel.setFont(AppTheme.FONT_HEADER_LARGE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Official printable clinical & administrative reports (JasperReports)");
        subLabel.setFont(AppTheme.FONT_BODY);
        subLabel.setForeground(AppTheme.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Center Body
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        // 3 Visual Report Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 18, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(0, 310));
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        cardsGrid.add(createDirectoryReportCard());
        cardsGrid.add(createPetHistoryReportCard());
        cardsGrid.add(createComprehensiveReportCard());

        centerBody.add(cardsGrid);
        centerBody.add(Box.createVerticalStrut(16));
        centerBody.add(createLiveStatsBar());

        add(centerBody, BorderLayout.CENTER);
    }

    /**
     * Card 1: Customer & Pet Directory Report
     */
    private ModernCard createDirectoryReportCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Visual Illustration (3D Icon)
        ImageIcon img = ImageUtil.loadScaledIcon("assets/report_clients.jpg", 68, 68);
        content.add(createRoundedImageLabel(img, 68));
        content.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Client & Pet Directory");
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(4));

        JLabel desc = new JLabel("Registered owners, contacts & companion pets");
        desc.setFont(AppTheme.FONT_SMALL);
        desc.setForeground(AppTheme.TEXT_MUTED);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(38)); // Balance height with Card 2 filter

        card.add(content, BorderLayout.CENTER);

        // Buttons Stacked (Full card width so text never truncates)
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        RoundButton btnPreview = new RoundButton("👁 View Report", RoundButton.ButtonStyle.PRIMARY);
        btnPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPreview.addActionListener(e -> controller.previewCustomerPetSummary(this));

        RoundButton btnExport = new RoundButton("📄 Export as PDF", RoundButton.ButtonStyle.SECONDARY);
        btnExport.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExport.addActionListener(e -> chooseExportLocation("Customer_Pet_Directory.pdf",
                file -> controller.exportCustomerPetSummary(this, file)));

        btnPanel.add(btnPreview);
        btnPanel.add(Box.createVerticalStrut(6));
        btnPanel.add(btnExport);

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Card 2: Pet Clinical Medical History
     */
    private ModernCard createPetHistoryReportCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Visual Illustration (3D Icon)
        ImageIcon img = ImageUtil.loadScaledIcon("assets/report_medical.jpg", 68, 68);
        content.add(createRoundedImageLabel(img, 68));
        content.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Pet Medical History");
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(4));

        JLabel desc = new JLabel("Clinical diagnoses, treatments & vet prescriptions");
        desc.setFont(AppTheme.FONT_SMALL);
        desc.setForeground(AppTheme.TEXT_MUTED);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(8));

        // Filter Dropdown
        cmbPetFilter = new JComboBox<>();
        cmbPetFilter.setFont(AppTheme.FONT_BODY);
        cmbPetFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbPetFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadPetFilterOptions();
        content.add(cmbPetFilter);

        card.add(content, BorderLayout.CENTER);

        // Buttons Stacked
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        RoundButton btnPreview = new RoundButton("👁 View Report", RoundButton.ButtonStyle.PRIMARY);
        btnPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPreview.addActionListener(e -> {
            PetOption selected = (PetOption) cmbPetFilter.getSelectedItem();
            int petId = selected != null ? selected.petId : 0;
            String petName = selected != null ? selected.displayName : "All Pets";
            controller.previewPetHistoryReport(this, petId, petName);
        });

        RoundButton btnExport = new RoundButton("📄 Export as PDF", RoundButton.ButtonStyle.SECONDARY);
        btnExport.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExport.addActionListener(e -> {
            PetOption selected = (PetOption) cmbPetFilter.getSelectedItem();
            int petId = selected != null ? selected.petId : 0;
            String prefix = selected != null && selected.petId > 0 ? selected.displayName.replaceAll("[^a-zA-Z0-9_]", "_") : "All_Pets";
            chooseExportLocation(prefix + "_Medical_History.pdf",
                    file -> controller.exportPetHistoryReport(this, petId, file));
        });

        btnPanel.add(btnPreview);
        btnPanel.add(Box.createVerticalStrut(6));
        btnPanel.add(btnExport);

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Card 3: Appointments & Visits Summary Report
     */
    private ModernCard createComprehensiveReportCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Visual Illustration (3D Icon)
        ImageIcon img = ImageUtil.loadScaledIcon("assets/report_appointments.jpg", 68, 68);
        content.add(createRoundedImageLabel(img, 68));
        content.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Appointments Summary");
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(4));

        JLabel desc = new JLabel("Schedules, service types & consultation audit");
        desc.setFont(AppTheme.FONT_SMALL);
        desc.setForeground(AppTheme.TEXT_MUTED);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(38)); // Balance height with Card 2 filter

        card.add(content, BorderLayout.CENTER);

        // Buttons Stacked
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        RoundButton btnPreview = new RoundButton("👁 View Report", RoundButton.ButtonStyle.PRIMARY);
        btnPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPreview.addActionListener(e -> controller.previewComprehensiveVisitReport(this));

        RoundButton btnExport = new RoundButton("📄 Export as PDF", RoundButton.ButtonStyle.SECONDARY);
        btnExport.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnExport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExport.addActionListener(e -> chooseExportLocation("Appointments_Summary_Report.pdf",
                file -> controller.exportComprehensiveVisitReport(this, file)));

        btnPanel.add(btnPreview);
        btnPanel.add(Box.createVerticalStrut(6));
        btnPanel.add(btnExport);

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Bottom Stats Bar (Clean & compact with Segoe UI Emoji icons).
     */
    private ModernCard createLiveStatsBar() {
        ModernCard card = new ModernCard(new FlowLayout(FlowLayout.CENTER, 16, 10));
        card.setBorder(new EmptyBorder(8, 16, 8, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        card.add(createStatPill("\uD83D\uDC65", "Clients", String.valueOf(controller.getCustomerCount()), new Color(0x37, 0x30, 0xA3)));
        card.add(createStatPill("\uD83D\uDC3E", "Pets", String.valueOf(controller.getPetCount()), new Color(0x06, 0x5F, 0x46)));
        card.add(createStatPill("\uD83D\uDCC5", "Appointments", String.valueOf(controller.getAppointmentCount()), new Color(0x92, 0x40, 0x0E)));
        card.add(createStatPill("\uD83D\uDC8A", "Treatments", String.valueOf(controller.getTreatmentCount()), new Color(0x6B, 0x21, 0xA8)));

        return card;
    }

    private JPanel createStatPill(String iconEmoji, String label, String value, Color color) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        pill.setOpaque(false);

        JLabel iconLbl = new JLabel(iconEmoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel textLbl = new JLabel(label + ": ");
        textLbl.setFont(AppTheme.FONT_SMALL);
        textLbl.setForeground(AppTheme.TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(AppTheme.FONT_BODY_BOLD);
        valLbl.setForeground(color);

        pill.add(iconLbl);
        pill.add(textLbl);
        pill.add(valLbl);
        return pill;
    }

    /**
     * Converts ImageIcon into a rounded badge.
     */
    private JLabel createRoundedImageLabel(ImageIcon icon, int size) {
        if (icon == null) {
            JLabel fallback = new JLabel("\uD83D\uDCCA");
            fallback.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            return fallback;
        }

        Image img = icon.getImage();
        BufferedImage rounded = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rounded.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(new RoundRectangle2D.Float(0, 0, size, size, 16, 16));
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(img, 0, 0, size, size, null);
        g2.dispose();

        JLabel lbl = new JLabel(new ImageIcon(rounded));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void loadPetFilterOptions() {
        cmbPetFilter.removeAllItems();
        cmbPetFilter.addItem(new PetOption(0, "All Registered Pets"));
        try {
            List<Pet> pets = controller.getPetsForFilter();
            for (Pet p : pets) {
                cmbPetFilter.addItem(new PetOption(p.getPetId(), p.getPetName() + " (" + p.getSpecies() + ")"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseExportLocation(String defaultFileName, ExportCallback callback) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setDialogTitle("Save Report as PDF");
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }
            callback.onFileChosen(selectedFile);
        }
    }

    @FunctionalInterface
    private interface ExportCallback {
        void onFileChosen(File file);
    }

    private static class PetOption {
        final int petId;
        final String displayName;

        PetOption(int petId, String displayName) {
            this.petId = petId;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
