package com.vetranest.view.treatment;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.TreatmentController;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Pet;
import com.vetranest.model.Treatment;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modal Dialog for adding or updating Clinical Treatment records & prescriptions.
 */
public class AddTreatmentDialog extends JDialog {

    private final TreatmentController controller;
    private final Runnable onSuccess;

    private JComboBox<Pet> cmbPet;
    private JTextField txtAppointmentId;
    private JTextField txtDate;
    private JTextField txtDiagnosis;
    private JTextArea txtMedication;
    private JTextArea txtInstructions;
    private JTextField txtDoctor;
    private JTextField txtCost;
    private JLabel lblError;

    private final int editTreatmentId;
    private final int presetPetId;
    private final int presetAppId;

    public AddTreatmentDialog(Frame parent, Runnable onSuccess) {
        this(parent, 0, 0, null, onSuccess);
    }

    public AddTreatmentDialog(Frame parent, int petId, int appId, Runnable onSuccess) {
        this(parent, petId, appId, null, onSuccess);
    }

    public AddTreatmentDialog(Frame parent, int petId, int appId, Treatment editTreatment, Runnable onSuccess) {
        super(parent, editTreatment == null ? "Record Clinical Treatment" : "Edit Treatment Record", true);
        this.controller = new TreatmentController();
        this.onSuccess = onSuccess;
        this.editTreatmentId = editTreatment != null ? editTreatment.getTreatmentId() : 0;
        this.presetPetId = petId;
        this.presetAppId = appId;

        initUI(editTreatment);
    }

    private void initUI(Treatment editTreatment) {
        setSize(520, 690);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.BG_CARD);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel(editTreatment == null ? "New Treatment & Prescription" : "Edit Treatment #" + editTreatmentId);
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(new Color(0xDC2626));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(10));

        // Pet selector
        panel.add(createLabel("Patient Pet *"));
        cmbPet = new JComboBox<>();
        cmbPet.setFont(AppTheme.FONT_BODY);
        cmbPet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbPet.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadPets();
        panel.add(cmbPet);
        panel.add(Box.createVerticalStrut(10));

        // Row: Appointment ID & Treatment Date
        JPanel rowGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        rowGrid.setOpaque(false);
        rowGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        rowGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel colApp = new JPanel(new BorderLayout(0, 4));
        colApp.setOpaque(false);
        colApp.add(createLabel("Appt. Ref (Optional)"), BorderLayout.NORTH);
        txtAppointmentId = new JTextField(presetAppId > 0 ? String.valueOf(presetAppId) : "");
        txtAppointmentId.setFont(AppTheme.FONT_BODY);
        txtAppointmentId.setPreferredSize(new Dimension(100, 36));
        colApp.add(txtAppointmentId, BorderLayout.CENTER);

        JPanel colDate = new JPanel(new BorderLayout(0, 4));
        colDate.setOpaque(false);
        colDate.add(createLabel("Treatment Date (YYYY-MM-DD) *"), BorderLayout.NORTH);
        String defaultDate = editTreatment != null && editTreatment.getTreatmentDate() != null
                ? editTreatment.getTreatmentDate().toString() : LocalDate.now().toString();
        txtDate = new JTextField(defaultDate);
        txtDate.setFont(AppTheme.FONT_BODY);
        txtDate.setPreferredSize(new Dimension(100, 36));
        colDate.add(txtDate, BorderLayout.CENTER);

        rowGrid.add(colApp);
        rowGrid.add(colDate);
        panel.add(rowGrid);
        panel.add(Box.createVerticalStrut(10));

        // Diagnosis
        panel.add(createLabel("Clinical Diagnosis *"));
        txtDiagnosis = new JTextField(editTreatment != null ? editTreatment.getDiagnosis() : "");
        txtDiagnosis.setFont(AppTheme.FONT_BODY);
        txtDiagnosis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtDiagnosis.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtDiagnosis);
        panel.add(Box.createVerticalStrut(10));

        // Medication
        panel.add(createLabel("Prescription / Medication *"));
        txtMedication = new JTextArea(editTreatment != null ? editTreatment.getMedication() : "", 3, 20);
        txtMedication.setFont(AppTheme.FONT_BODY);
        txtMedication.setLineWrap(true);
        txtMedication.setWrapStyleWord(true);
        JScrollPane scrollMed = new JScrollPane(txtMedication);
        scrollMed.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollMed.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollMed);
        panel.add(Box.createVerticalStrut(10));

        // Instructions
        panel.add(createLabel("Special Care Instructions / Diet"));
        txtInstructions = new JTextArea(editTreatment != null ? editTreatment.getInstructions() : "", 2, 20);
        txtInstructions.setFont(AppTheme.FONT_BODY);
        txtInstructions.setLineWrap(true);
        txtInstructions.setWrapStyleWord(true);
        JScrollPane scrollInst = new JScrollPane(txtInstructions);
        scrollInst.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        scrollInst.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollInst);
        panel.add(Box.createVerticalStrut(10));

        // Attending Vet
        panel.add(createLabel("Attending Veterinarian"));
        txtDoctor = new JTextField(editTreatment != null ? editTreatment.getDoctorName() : "Dr. K. Perera (BVSc)");
        txtDoctor.setFont(AppTheme.FONT_BODY);
        txtDoctor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtDoctor.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtDoctor);
        panel.add(Box.createVerticalStrut(10));

        // Treatment Fee
        panel.add(createLabel("Treatment Fee / Service Charge (LKR)"));
        txtCost = new JTextField("2500.00");
        txtCost.setFont(AppTheme.FONT_BODY);
        txtCost.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtCost.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtCost);
        panel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundButton btnCancel = new RoundButton("Cancel", RoundButton.ButtonStyle.SECONDARY);
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> dispose());

        RoundButton btnSave = new RoundButton(editTreatment == null ? "Save Record" : "Update Record", RoundButton.ButtonStyle.PRIMARY);
        btnSave.setPreferredSize(new Dimension(140, 38));
        btnSave.addActionListener(e -> saveTreatment());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        panel.add(btnPanel);

        getContentPane().add(panel);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(AppTheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void loadPets() {
        try {
            List<Pet> pets = controller.getAllPets();
            cmbPet.removeAllItems();
            int selectIndex = 0;
            for (int i = 0; i < pets.size(); i++) {
                Pet p = pets.get(i);
                cmbPet.addItem(p);
                if (p.getPetId() == presetPetId) {
                    selectIndex = i;
                }
            }
            if (!pets.isEmpty()) {
                cmbPet.setSelectedIndex(selectIndex);
            }
        } catch (Exception e) {
            lblError.setText("Failed to load pet profiles: " + e.getMessage());
        }
    }

    private void saveTreatment() {
        Pet selectedPet = (Pet) cmbPet.getSelectedItem();
        if (selectedPet == null) {
            lblError.setText("Please select a valid pet.");
            return;
        }

        int petId = selectedPet.getPetId();
        int appId = 0;
        try {
            if (!txtAppointmentId.getText().trim().isEmpty()) {
                appId = Integer.parseInt(txtAppointmentId.getText().trim());
            }
        } catch (NumberFormatException ignored) {}

        String dateStr = txtDate.getText().trim();
        String diagnosis = txtDiagnosis.getText().trim();
        String medication = txtMedication.getText().trim();
        String instructions = txtInstructions.getText().trim();
        String doctor = txtDoctor.getText().trim();

        double cost = 0.0;
        try {
            if (txtCost != null) {
                String costStr = txtCost.getText().trim().replaceAll("[^0-9.]", "");
                if (!costStr.isEmpty()) cost = Double.parseDouble(costStr);
            }
        } catch (Exception ignored) {}

        try {
            boolean success;
            if (editTreatmentId == 0) {
                success = controller.addTreatment(petId, appId, dateStr, diagnosis, medication, instructions, doctor, cost);
            } else {
                success = controller.updateTreatment(editTreatmentId, dateStr, diagnosis, medication, instructions, doctor);
            }

            if (success) {
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                lblError.setText("Failed to save clinical record into database.");
            }
        } catch (ValidationException ve) {
            lblError.setText(ve.getMessage());
        } catch (Exception ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }
}
