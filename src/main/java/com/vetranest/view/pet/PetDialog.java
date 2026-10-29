package com.vetranest.view.pet;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.PetController;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Customer;
import com.vetranest.model.Pet;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Modal dialog for registering or modifying Pet records.
 * Organized with 2-column rows to prevent vertical overflow and maintain clean alignment.
 */
public class PetDialog extends JDialog {

    private final PetController controller;
    private final Pet existingPet;
    private final Runnable onSuccess;

    private JTextField txtCode;
    private JTextField txtName;
    private JComboBox<Customer> cmbOwner;
    private JComboBox<String> cmbSpecies;
    private JTextField txtBreed;
    private JComboBox<String> cmbGender;
    private JTextField txtDob;
    private JTextField txtWeight;
    private JTextField txtColor;
    private JLabel lblError;

    public PetDialog(Frame parent, Pet pet, Runnable onSuccess) {
        super(parent, pet == null ? "Add New Pet" : "Edit Pet - " + pet.getPetName(), true);
        this.controller = new PetController();
        this.existingPet = pet;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setSize(540, 560);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JLabel titleLabel = new JLabel(existingPet == null ? "Register New Pet" : "Edit Pet Information");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Associate pet patient with a registered customer");
        subLabel.setFont(AppTheme.FONT_SMALL);
        subLabel.setForeground(AppTheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCode = createFieldComponent();
        txtCode.setEditable(false);
        txtCode.setBackground(new Color(0xF1, 0xF5, 0xF9));
        txtCode.setText(existingPet != null ? existingPet.getPetCode() : controller.getNextPetCode());

        txtName = createFieldComponent();

        // Customer Owner Dropdown
        cmbOwner = new JComboBox<>();
        cmbOwner.setFont(AppTheme.FONT_BODY);
        cmbOwner.setPreferredSize(new Dimension(100, 36));
        loadCustomerDropdown();

        // Species & Breed
        cmbSpecies = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit", "Exotic"});
        cmbSpecies.setFont(AppTheme.FONT_BODY);
        cmbSpecies.setPreferredSize(new Dimension(100, 36));

        txtBreed = createFieldComponent();
        txtBreed.setText("Golden Retriever");

        cmbGender = new JComboBox<>(new String[]{"Male", "Female"});
        cmbGender.setFont(AppTheme.FONT_BODY);
        cmbGender.setPreferredSize(new Dimension(100, 36));

        txtDob = createFieldComponent();
        txtDob.setText("2024-01-15");

        txtWeight = createFieldComponent();
        txtWeight.setText("5.5");

        txtColor = createFieldComponent();
        txtColor.setText("Golden Cream");

        // Prepopulate if editing
        if (existingPet != null) {
            txtName.setText(existingPet.getPetName());
            cmbSpecies.setSelectedItem(existingPet.getSpecies());
            txtBreed.setText(existingPet.getBreed());
            cmbGender.setSelectedItem(existingPet.getGender());
            if (existingPet.getDob() != null) {
                txtDob.setText(existingPet.getDob().toString());
            }
            txtWeight.setText(String.valueOf(existingPet.getWeight()));
            txtColor.setText(existingPet.getColor());
        }

        // Action Buttons
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundButton btnCancel = new RoundButton("Cancel", RoundButton.ButtonStyle.NEUTRAL);
        btnCancel.setPreferredSize(new Dimension(100, 42));
        btnCancel.addActionListener(e -> dispose());

        RoundButton btnSave = new RoundButton(existingPet == null ? "Save Pet" : "Update Pet", RoundButton.ButtonStyle.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 42));
        btnSave.addActionListener(e -> performSave());

        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        // Header
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(4));

        // Form Fields (Clean 2-column and full-width layout)
        panel.add(createTwoColumnRow("Pet Code", txtCode, "Pet Name *", txtName));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createSingleRow("Select Owner (Customer) *", cmbOwner));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createTwoColumnRow("Species *", cmbSpecies, "Breed", txtBreed));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createTwoColumnRow("Gender", cmbGender, "Date of Birth (YYYY-MM-DD)", txtDob));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createTwoColumnRow("Weight (kg)", txtWeight, "Color / Markings", txtColor));
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonRow);

        setContentPane(panel);
    }

    private void loadCustomerDropdown() {
        try {
            List<Customer> customers = controller.getAllCustomersForSelection();
            for (Customer c : customers) {
                cmbOwner.addItem(c);
                if (existingPet != null && c.getCustomerId() == existingPet.getCustomerId()) {
                    cmbOwner.setSelectedItem(c);
                }
            }
        } catch (Exception ex) {
            lblError.setText("Error loading customers: " + ex.getMessage());
        }
    }

    private JPanel createSingleRow(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createTwoColumnRow(String label1, JComponent comp1, String label2, JComponent comp2) {
        JPanel grid = new JPanel(new GridLayout(1, 2, 12, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel col1 = new JPanel(new BorderLayout(0, 4));
        col1.setOpaque(false);
        JLabel l1 = new JLabel(label1);
        l1.setFont(AppTheme.FONT_BODY_BOLD);
        l1.setForeground(AppTheme.TEXT_PRIMARY);
        col1.add(l1, BorderLayout.NORTH);
        col1.add(comp1, BorderLayout.CENTER);

        JPanel col2 = new JPanel(new BorderLayout(0, 4));
        col2.setOpaque(false);
        JLabel l2 = new JLabel(label2);
        l2.setFont(AppTheme.FONT_BODY_BOLD);
        l2.setForeground(AppTheme.TEXT_PRIMARY);
        col2.add(l2, BorderLayout.NORTH);
        col2.add(comp2, BorderLayout.CENTER);

        grid.add(col1);
        grid.add(col2);
        return grid;
    }

    private JTextField createFieldComponent() {
        JTextField tf = new JTextField();
        tf.setFont(AppTheme.FONT_BODY);
        tf.setPreferredSize(new Dimension(100, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void performSave() {
        lblError.setText(" ");
        Customer selectedCustomer = (Customer) cmbOwner.getSelectedItem();
        if (selectedCustomer == null) {
            lblError.setText("⚠️ Please select an owner customer.");
            return;
        }

        String name = txtName.getText().trim();
        String species = (String) cmbSpecies.getSelectedItem();
        String breed = txtBreed.getText().trim();
        String gender = (String) cmbGender.getSelectedItem();
        String color = txtColor.getText().trim();

        Date dob = null;
        try {
            if (!txtDob.getText().trim().isEmpty()) {
                dob = Date.valueOf(txtDob.getText().trim());
            }
        } catch (Exception e) {
            lblError.setText("⚠️ Date format must be YYYY-MM-DD.");
            return;
        }

        double weight = 0.0;
        try {
            if (!txtWeight.getText().trim().isEmpty()) {
                weight = Double.parseDouble(txtWeight.getText().trim());
            }
        } catch (Exception e) {
            lblError.setText("⚠️ Weight must be a numeric value.");
            return;
        }

        try {
            boolean ok;
            if (existingPet == null) {
                ok = controller.addPet(selectedCustomer.getCustomerId(), name, species, breed, gender, dob, weight, color);
            } else {
                ok = controller.updatePet(existingPet.getPetId(), selectedCustomer.getCustomerId(), name, species, breed, gender, dob, weight, color);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Pet " + (existingPet == null ? "registered" : "updated") + " successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            }
        } catch (ValidationException ex) {
            lblError.setText("⚠️ " + ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Database error: " + ex.getMessage());
        }
    }
}
