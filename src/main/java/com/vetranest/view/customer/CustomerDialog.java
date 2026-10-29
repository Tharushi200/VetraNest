package com.vetranest.view.customer;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.CustomerController;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Customer;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for Adding and Editing Customers.
 * Input UI matching NIBM Coursework specification with left-aligned, full-width fields.
 */
public class CustomerDialog extends JDialog {

    private final CustomerController controller;
    private final Customer existingCustomer;
    private final Runnable onSuccess;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JLabel lblError;

    public CustomerDialog(Frame parent, Customer customer, Runnable onSuccess) {
        super(parent, customer == null ? "Add New Customer" : "Edit Customer - " + customer.getName(), true);
        this.controller = new CustomerController();
        this.existingCustomer = customer;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setSize(480, 540);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JLabel titleLabel = new JLabel(existingCustomer == null ? "Add New Customer" : "Edit Customer Details");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Enter pet owner contact and billing information");
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
        txtCode.setText(existingCustomer != null ? existingCustomer.getCustomerCode() : controller.getNextCustomerCode());

        txtName = createFieldComponent();
        txtEmail = createFieldComponent();
        txtPhone = createFieldComponent();
        txtAddress = createFieldComponent();

        if (existingCustomer != null) {
            txtName.setText(existingCustomer.getName());
            txtEmail.setText(existingCustomer.getEmail());
            txtPhone.setText(existingCustomer.getPhone());
            txtAddress.setText(existingCustomer.getAddress());
        }

        // Action Buttons Row (Side by side)
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundButton btnCancel = new RoundButton("Cancel", RoundButton.ButtonStyle.NEUTRAL);
        btnCancel.setPreferredSize(new Dimension(100, 42));
        btnCancel.addActionListener(e -> dispose());

        RoundButton btnSave = new RoundButton(existingCustomer == null ? "Save Customer" : "Update Customer", RoundButton.ButtonStyle.PRIMARY);
        btnSave.setPreferredSize(new Dimension(100, 42));
        btnSave.addActionListener(e -> performSave());

        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(6));
        panel.add(createRow("Customer Code", txtCode));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Full Name *", txtName));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Email Address *", txtEmail));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Phone Number *", txtPhone));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createRow("Residential Address", txtAddress));
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonRow);

        setContentPane(panel);
    }

    private JPanel createRow(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField createFieldComponent() {
        JTextField tf = new JTextField();
        tf.setFont(AppTheme.FONT_BODY);
        tf.setPreferredSize(new Dimension(100, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    private void performSave() {
        lblError.setText(" ");
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        try {
            boolean ok;
            if (existingCustomer == null) {
                ok = controller.addCustomer(name, email, phone, address);
            } else {
                ok = controller.updateCustomer(existingCustomer.getCustomerId(), name, email, phone, address);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Customer " + (existingCustomer == null ? "added" : "updated") + " successfully!",
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
