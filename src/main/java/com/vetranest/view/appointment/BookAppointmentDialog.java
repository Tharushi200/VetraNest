package com.vetranest.view.appointment;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AppointmentController;
import com.vetranest.exception.AppointmentConflictException;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Customer;
import com.vetranest.model.Pet;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modal Dialog for booking new appointments with conflict checking.
 * Clean, left-aligned layout with side-by-side action buttons.
 */
public class BookAppointmentDialog extends JDialog {

    private final AppointmentController controller;
    private final Runnable onSuccess;

    private JComboBox<Customer> cmbCustomer;
    private JComboBox<Pet> cmbPet;
    private JTextField txtDate;
    private JComboBox<String> cmbTimeSlot;
    private JComboBox<String> cmbService;
    private JTextField txtNotes;
    private JLabel lblError;

    // For editing existing appointment
    private final int editAppointmentId;
    private final String editStatus;

    public BookAppointmentDialog(Frame parent, Runnable onSuccess) {
        this(parent, onSuccess, 0, null, null, null, null, null, null);
    }

    public BookAppointmentDialog(Frame parent, Runnable onSuccess,
                                  int editId, String editDate, String editSlot,
                                  String editService, String editStatus,
                                  Integer editCustomerId, Integer editPetId) {
        super(parent, editId == 0 ? "Book New Appointment" : "Edit Appointment", true);
        this.controller = new AppointmentController();
        this.onSuccess = onSuccess;
        this.editAppointmentId = editId;
        this.editStatus = editStatus;
        initUI(editDate, editSlot, editService, editCustomerId, editPetId);
    }

    private void initUI(String editDate, String editSlot, String editService,
                        Integer editCustomerId, Integer editPetId) {
        setSize(520, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Clean Title without broken emoji
        JLabel titleLabel = new JLabel(editAppointmentId == 0 ? "Book New Appointment" : "Edit Appointment");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Select patient, service and schedule a convenient time slot");
        subLabel.setFont(AppTheme.FONT_SMALL);
        subLabel.setForeground(AppTheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Customer Dropdown
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(AppTheme.FONT_BODY);
        cmbCustomer.setPreferredSize(new Dimension(100, 36));

        // Pet Dropdown (filtered by owner selection)
        cmbPet = new JComboBox<>();
        cmbPet.setFont(AppTheme.FONT_BODY);
        cmbPet.setPreferredSize(new Dimension(100, 36));

        // Date Field
        txtDate = new JTextField(LocalDate.now().toString());
        txtDate.setFont(AppTheme.FONT_BODY);
        txtDate.setPreferredSize(new Dimension(100, 36));
        styleField(txtDate);

        // Time Slot
        cmbTimeSlot = new JComboBox<>(controller.getAvailableTimeSlots());
        cmbTimeSlot.setFont(AppTheme.FONT_BODY);
        cmbTimeSlot.setPreferredSize(new Dimension(100, 36));

        // Service Type
        cmbService = new JComboBox<>(new String[]{
                "Vaccination", "Checkup", "Grooming", "Treatment", "Surgery", "Dental"
        });
        cmbService.setFont(AppTheme.FONT_BODY);
        cmbService.setPreferredSize(new Dimension(100, 36));

        // Notes
        txtNotes = new JTextField();
        txtNotes.setFont(AppTheme.FONT_BODY);
        txtNotes.setPreferredSize(new Dimension(100, 36));
        styleField(txtNotes);

        // Load dropdowns
        loadCustomers(editCustomerId);
        loadAllPets(editPetId);

        // Prefill edit values
        if (editDate != null) txtDate.setText(editDate);
        if (editSlot != null) cmbTimeSlot.setSelectedItem(editSlot);
        if (editService != null) cmbService.setSelectedItem(editService);

        // Buttons
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundButton btnCancel = new RoundButton("Cancel", RoundButton.ButtonStyle.NEUTRAL);
        btnCancel.setPreferredSize(new Dimension(100, 42));
        btnCancel.addActionListener(e -> dispose());

        RoundButton btnBook = new RoundButton(
                editAppointmentId == 0 ? "Book Appointment" : "Update Appointment",
                RoundButton.ButtonStyle.SECONDARY);
        btnBook.setPreferredSize(new Dimension(100, 42));
        btnBook.addActionListener(e -> performBook());

        buttonRow.add(btnCancel);
        buttonRow.add(btnBook);

        // Assemble Panel
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(4));

        panel.add(createSingleRow("Select Customer (Owner) *", cmbCustomer));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createSingleRow("Select Pet Patient *", cmbPet));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createTwoColumnRow("Appointment Date (YYYY-MM-DD) *", txtDate, "Time Slot *", cmbTimeSlot));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createTwoColumnRow("Service Type *", cmbService, "Notes / Instructions", txtNotes));
        panel.add(Box.createVerticalStrut(22));
        panel.add(buttonRow);

        setContentPane(panel);
    }

    private void loadCustomers(Integer editCustomerId) {
        try {
            List<Customer> customers = controller.getAllCustomers();
            for (Customer c : customers) {
                cmbCustomer.addItem(c);
                if (editCustomerId != null && c.getCustomerId() == editCustomerId) {
                    cmbCustomer.setSelectedItem(c);
                }
            }
        } catch (Exception ex) {
            lblError.setText("Error loading customers: " + ex.getMessage());
        }
    }

    private void loadAllPets(Integer editPetId) {
        try {
            cmbPet.removeAllItems();
            List<Pet> pets = controller.getAllPets();
            for (Pet p : pets) {
                cmbPet.addItem(p);
                if (editPetId != null && p.getPetId() == editPetId) {
                    cmbPet.setSelectedItem(p);
                }
            }
        } catch (Exception ex) {
            lblError.setText("Error loading pets: " + ex.getMessage());
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

    private void styleField(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void performBook() {
        lblError.setText(" ");
        Customer customer = (Customer) cmbCustomer.getSelectedItem();
        Pet pet = (Pet) cmbPet.getSelectedItem();

        if (customer == null || pet == null) {
            lblError.setText("⚠️ Please select both a customer and a pet.");
            return;
        }

        String date = txtDate.getText().trim();
        String slot = (String) cmbTimeSlot.getSelectedItem();
        String service = (String) cmbService.getSelectedItem();
        String notes = txtNotes.getText().trim();

        try {
            boolean ok;
            if (editAppointmentId == 0) {
                ok = controller.bookAppointment(customer.getCustomerId(), pet.getPetId(),
                        date, slot, service, notes);
            } else {
                ok = controller.updateAppointment(editAppointmentId, customer.getCustomerId(),
                        pet.getPetId(), date, slot, service,
                        editStatus != null ? editStatus : "Pending", notes);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Appointment " + (editAppointmentId == 0 ? "booked" : "updated") + " successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) onSuccess.run();
            }

        } catch (AppointmentConflictException ex) {
            // === Explicit User-Defined Exception Demo (Rubric) ===
            lblError.setText("⚠️ " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "⚠️ APPOINTMENT CONFLICT DETECTED!\n\n" + ex.getMessage() +
                    "\n\nPlease select a different time slot.",
                    "Appointment Conflict Error", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            lblError.setText("⚠️ " + ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }
}
