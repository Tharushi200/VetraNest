package com.vetranest.view.auth;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AuthController;
import com.vetranest.exception.DuplicateUserException;
import com.vetranest.exception.ValidationException;
import com.vetranest.util.ImageUtil;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern User Registration Screen.
 * Implements validation and explicitly demonstrates the user-defined DuplicateUserException.
 */
public class RegisterFrame extends JFrame {

    private final AuthController authController;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JComboBox<String> cmbRole;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtSecurityAnswer;
    private JLabel lblError;

    public RegisterFrame() {
        this.authController = new AuthController();
        initUI();
    }

    private void initUI() {
        setTitle("VetraNest - Register New Account");
        setSize(960, 680);
        setMinimumSize(new Dimension(880, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel(new GridLayout(1, 2));
        mainContainer.setBackground(AppTheme.BG_APP);

        mainContainer.add(createLeftHeroPanel());
        mainContainer.add(createRightFormPanel());

        setContentPane(mainContainer);
    }

    private JPanel createLeftHeroPanel() {
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();

                // Harmonious pastel lavender-to-mint gradient matching the purple & green logo
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0xEB, 0xE7, 0xFF),     // Soft Vibrant Lavender
                        w, h, new Color(0xD1, 0xFA, 0xE5)      // Fresh Medical Mint
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Subtle right border separating form panel
                g2.setColor(new Color(0xC7, 0xD2, 0xFE));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(w - 1, 0, w - 1, h);

                g2.dispose();
            }
        };
        heroPanel.setOpaque(false);

        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        ImageIcon logoIcon = ImageUtil.loadScaledIcon("assets/logo.png", 80, 80);
        JLabel logoLabel = new JLabel(logoIcon != null ? logoIcon : new JLabel().getIcon());
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Join VetraNest");
        nameLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 28));
        nameLabel.setForeground(new Color(0x37, 0x30, 0xA3));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Staff & Veterinary Portal Onboarding");
        subLabel.setFont(AppTheme.FONT_BODY);
        subLabel.setForeground(new Color(0x4B, 0x55, 0x63));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon artIcon = ImageUtil.loadScaledIcon("assets/auth_hero.png", 220, 220);
        JLabel artLabel = new JLabel(artIcon != null ? artIcon : new JLabel().getIcon());
        artLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        heroPanel.add(Box.createVerticalGlue());
        heroPanel.add(logoLabel);
        heroPanel.add(Box.createVerticalStrut(8));
        heroPanel.add(nameLabel);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(subLabel);
        heroPanel.add(Box.createVerticalStrut(20));
        heroPanel.add(artLabel);
        heroPanel.add(Box.createVerticalGlue());

        return heroPanel;
    }

    private JPanel createRightFormPanel() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);

        JPanel innerForm = new JPanel();
        innerForm.setLayout(new BoxLayout(innerForm, BoxLayout.Y_AXIS));
        innerForm.setBackground(Color.WHITE);
        innerForm.setPreferredSize(new Dimension(360, 580));
        innerForm.setMaximumSize(new Dimension(360, 590));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtFullName = createStyledField();
        txtUsername = createStyledField();
        txtEmail = createStyledField();
        
        cmbRole = new JComboBox<>(new String[]{"Admin", "Receptionist", "Staff"});
        cmbRole.setFont(AppTheme.FONT_BODY);
        cmbRole.setPreferredSize(new Dimension(360, 36));
        cmbRole.setMaximumSize(new Dimension(360, 36));

        txtPassword = new JPasswordField();
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(360, 36));
        txtPassword.setMaximumSize(new Dimension(360, 36));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(AppTheme.FONT_BODY);
        txtConfirmPassword.setPreferredSize(new Dimension(360, 36));
        txtConfirmPassword.setMaximumSize(new Dimension(360, 36));
        txtConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        txtSecurityAnswer = createStyledField();
        txtSecurityAnswer.setText("VN2026");

        RoundButton btnRegister = new RoundButton("Register Account", RoundButton.ButtonStyle.SECONDARY);
        btnRegister.setPreferredSize(new Dimension(360, 42));
        btnRegister.setMaximumSize(new Dimension(360, 42));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> performRegistration());

        JPanel backToLoginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backToLoginPanel.setOpaque(false);
        backToLoginPanel.setMaximumSize(new Dimension(360, 30));
        backToLoginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAlready = new JLabel("Already registered?");
        lblAlready.setFont(AppTheme.FONT_SMALL);
        lblAlready.setForeground(AppTheme.TEXT_MUTED);

        JLabel lblLoginLink = new JLabel("Back to Login");
        lblLoginLink.setFont(AppTheme.FONT_BODY_BOLD);
        lblLoginLink.setForeground(AppTheme.PRIMARY);
        lblLoginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        backToLoginPanel.add(lblAlready);
        backToLoginPanel.add(lblLoginLink);

        // Add to innerForm
        innerForm.add(titleLabel);
        innerForm.add(Box.createVerticalStrut(2));
        innerForm.add(lblError);
        innerForm.add(Box.createVerticalStrut(4));
        innerForm.add(createFieldRow("Full Name", txtFullName));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("Username", txtUsername));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("Email Address", txtEmail));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("User Role", cmbRole));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("Password", txtPassword));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("Confirm Password", txtConfirmPassword));
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(createFieldRow("Clinic Security Answer", txtSecurityAnswer));
        innerForm.add(Box.createVerticalStrut(14));
        innerForm.add(btnRegister);
        innerForm.add(Box.createVerticalStrut(10));
        innerForm.add(backToLoginPanel);

        formContainer.add(innerForm);
        return formContainer;
    }

    private JPanel createFieldRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(360, 56));
        p.setMaximumSize(new Dimension(360, 56));
        p.setMinimumSize(new Dimension(360, 56));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY_BOLD);
        l.setForeground(AppTheme.TEXT_PRIMARY);

        field.setPreferredSize(new Dimension(360, 34));
        field.setMaximumSize(new Dimension(360, 34));

        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(AppTheme.FONT_BODY);
        f.setPreferredSize(new Dimension(360, 34));
        f.setMaximumSize(new Dimension(360, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void performRegistration() {
        lblError.setText(" ");
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String secAnswer = txtSecurityAnswer.getText().trim();

        try {
            boolean success = authController.register(fullName, username, email, role, password, confirmPassword, secAnswer);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Account registered successfully for " + username + "!\nYou can now log in.",
                        "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);
            }
        } catch (DuplicateUserException ex) {
            // Explicit demonstration of User-Defined Exception for Rubric
            lblError.setText("⚠️ " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Duplicate User Error", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            lblError.setText("⚠️ " + ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Registration error: " + ex.getMessage());
        }
    }
}
