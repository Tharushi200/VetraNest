package com.vetranest.view.auth;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AuthController;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern modal dialog for password recovery via security verification.
 */
public class ForgotPasswordDialog extends JDialog {

    private final AuthController authController;
    private JTextField txtUsername;
    private JTextField txtSecurityAnswer;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JLabel lblError;

    public ForgotPasswordDialog(Frame owner) {
        super(owner, "Reset Password - VetraNest", true);
        this.authController = new AuthController();
        initUI();
    }

    private void initUI() {
        setSize(440, 480);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Verify security credentials to create a new password");
        subLabel.setFont(AppTheme.FONT_SMALL);
        subLabel.setForeground(AppTheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtSecurityAnswer = new JTextField("VN2026");
        txtNewPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        RoundButton btnSubmit = new RoundButton("Reset Password", RoundButton.ButtonStyle.PRIMARY);
        btnSubmit.setPreferredSize(new Dimension(380, 42));
        btnSubmit.setMaximumSize(new Dimension(380, 42));
        btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSubmit.addActionListener(e -> performReset());

        RoundButton btnCancel = new RoundButton("Cancel", RoundButton.ButtonStyle.NEUTRAL);
        btnCancel.setPreferredSize(new Dimension(380, 38));
        btnCancel.setMaximumSize(new Dimension(380, 38));
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.addActionListener(e -> dispose());

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(subLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(lblError);
        mainPanel.add(createField("Username", txtUsername));
        mainPanel.add(createField("Clinic Security Answer (Default: VN2026)", txtSecurityAnswer));
        mainPanel.add(createField("New Password", txtNewPassword));
        mainPanel.add(createField("Confirm New Password", txtConfirmPassword));
        mainPanel.add(Box.createVerticalStrut(14));
        mainPanel.add(btnSubmit);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(btnCancel);

        setContentPane(mainPanel);
    }

    private JPanel createField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(380, 56));
        p.setMaximumSize(new Dimension(380, 56));
        p.setMinimumSize(new Dimension(380, 56));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);

        comp.setFont(AppTheme.FONT_BODY);
        comp.setPreferredSize(new Dimension(380, 34));
        comp.setMaximumSize(new Dimension(380, 34));

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void performReset() {
        lblError.setText(" ");
        String username = txtUsername.getText().trim();
        String secAnswer = txtSecurityAnswer.getText().trim();
        String newPass = new String(txtNewPassword.getPassword()).trim();
        String confPass = new String(txtConfirmPassword.getPassword()).trim();

        try {
            boolean success = authController.resetPassword(username, secAnswer, newPass, confPass);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Password has been reset successfully! You may now log in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                lblError.setText("Username or Security Answer is incorrect.");
            }
        } catch (Exception ex) {
            lblError.setText("⚠️ " + ex.getMessage());
        }
    }
}
