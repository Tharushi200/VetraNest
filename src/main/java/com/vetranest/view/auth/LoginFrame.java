package com.vetranest.view.auth;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AuthController;
import com.vetranest.model.User;
import com.vetranest.util.ImageUtil;
import com.vetranest.view.components.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Two-Column Login Screen.
 * Left Side: Brand Logo, Illustration & Tagline.
 * Right Side: Modern Login Form with Role Switcher (Admin / Doctor) and Password Visibility Toggle.
 */
public class LoginFrame extends JFrame {

    private final AuthController authController;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkRememberMe;
    private JLabel lblError;
    private RoundButton btnLogin;

    // Password visibility state
    private boolean passwordVisible = false;
    private char defaultEchoChar;

    public LoginFrame() {
        this.authController = new AuthController();
        initUI();
    }

    private void initUI() {
        setTitle("VetraNest - Login");
        setSize(980, 640);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Container (Split into Left Hero Panel & Right Form Panel)
        JPanel mainContainer = new JPanel(new GridLayout(1, 2));
        mainContainer.setBackground(AppTheme.BG_APP);

        mainContainer.add(createLeftHeroPanel());
        mainContainer.add(createRightFormPanel());

        setContentPane(mainContainer);
    }

    /**
     * Left Column: Branding, Logo and Vector Hero Art.
     */
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

        // Logo
        ImageIcon logoIcon = ImageUtil.loadScaledIcon("assets/logo.png", 90, 90);
        JLabel logoLabel = new JLabel(logoIcon != null ? logoIcon : new JLabel().getIcon());
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App Name (Deep Indigo matching logo emblem)
        JLabel nameLabel = new JLabel("VetraNest");
        nameLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 28));
        nameLabel.setForeground(new Color(0x37, 0x30, 0xA3));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel typeLabel = new JLabel("Veterinary Management System");
        typeLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 13));
        typeLabel.setForeground(new Color(0x4B, 0x55, 0x63));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline (Deep Emerald matching medical cross)
        JLabel tagLabel = new JLabel("Healthy Pets  •  Happy Lives");
        tagLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 12));
        tagLabel.setForeground(new Color(0x05, 0x96, 0x69));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hero Illustration (Transparent PNG)
        ImageIcon artIcon = ImageUtil.loadScaledIcon("assets/auth_hero.png", 260, 260);
        JLabel artLabel = new JLabel(artIcon != null ? artIcon : new JLabel().getIcon());
        artLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        heroPanel.add(Box.createVerticalGlue());
        heroPanel.add(logoLabel);
        heroPanel.add(Box.createVerticalStrut(10));
        heroPanel.add(nameLabel);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(typeLabel);
        heroPanel.add(Box.createVerticalStrut(2));
        heroPanel.add(tagLabel);
        heroPanel.add(Box.createVerticalStrut(25));
        heroPanel.add(artLabel);
        heroPanel.add(Box.createVerticalGlue());

        return heroPanel;
    }

    /**
     * Right Column: Clean White Login Card with Role Selector, Form Fields and Password Eye Toggle.
     */
    private JPanel createRightFormPanel() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);

        JPanel innerForm = new JPanel();
        innerForm.setLayout(new BoxLayout(innerForm, BoxLayout.Y_AXIS));
        innerForm.setBackground(Color.WHITE);
        innerForm.setPreferredSize(new Dimension(360, 420));
        innerForm.setMaximumSize(new Dimension(380, 440));

        // Login Heading
        JLabel welcomeLabel = new JLabel("Login");
        welcomeLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 28));
        welcomeLabel.setForeground(AppTheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Welcome back! Please enter your details to sign in");
        subtitleLabel.setFont(AppTheme.FONT_BODY);
        subtitleLabel.setForeground(AppTheme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error message banner
        lblError = new JLabel(" ");
        lblError.setFont(AppTheme.FONT_SMALL);
        lblError.setForeground(AppTheme.DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── USERNAME FIELD ──
        JPanel userRow = new JPanel(new BorderLayout(0, 4));
        userRow.setOpaque(false);
        userRow.setPreferredSize(new Dimension(360, 66));
        userRow.setMaximumSize(new Dimension(360, 66));
        userRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(AppTheme.FONT_BODY_BOLD);
        lblUser.setForeground(AppTheme.TEXT_PRIMARY);

        txtUsername = new JTextField("admin");
        txtUsername.setFont(AppTheme.FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(360, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        userRow.add(lblUser, BorderLayout.NORTH);
        userRow.add(txtUsername, BorderLayout.CENTER);

        // ── PASSWORD FIELD WITH EYE TOGGLE ──
        JPanel passRow = new JPanel(new BorderLayout(0, 4));
        passRow.setOpaque(false);
        passRow.setPreferredSize(new Dimension(360, 66));
        passRow.setMaximumSize(new Dimension(360, 66));
        passRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(AppTheme.FONT_BODY_BOLD);
        lblPass.setForeground(AppTheme.TEXT_PRIMARY);

        // Password input box containing field + eye button
        JPanel passBox = new JPanel(new BorderLayout());
        passBox.setBackground(Color.WHITE);
        passBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(0, 12, 0, 4)
        ));
        passBox.setPreferredSize(new Dimension(360, 40));

        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(AppTheme.FONT_BODY);
        txtPassword.setBorder(null);
        txtPassword.setOpaque(false);
        defaultEchoChar = txtPassword.getEchoChar();

        // Eye toggle button (vector rendered with Graphics2D for pixel-perfect sharpness)
        JButton btnEye = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int h = getHeight();
                int cx = w / 2;
                int cy = h / 2;

                Color iconColor = passwordVisible ? AppTheme.PRIMARY : new Color(0x94, 0xA3, 0xB8);
                if (getModel().isRollover()) {
                    iconColor = passwordVisible ? AppTheme.PRIMARY_HOVER : AppTheme.TEXT_PRIMARY;
                }
                g2.setColor(iconColor);

                // Eye Contour
                int ew = 18;
                int eh = 10;
                Path2D.Double eye = new Path2D.Double();
                eye.moveTo(cx - ew / 2.0, cy);
                eye.quadTo(cx, cy - eh, cx + ew / 2.0, cy);
                eye.quadTo(cx, cy + eh, cx - ew / 2.0, cy);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(eye);

                // Pupil
                int pr = 3;
                g2.fillOval(cx - pr, cy - pr, pr * 2, pr * 2);

                // Diagonal slash across the eye when password is shown (to indicate click to hide)
                if (passwordVisible) {
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - ew / 2 + 1, cy + eh - 2, cx + ew / 2 - 1, cy - eh + 2);
                }

                g2.dispose();
            }
        };
        btnEye.setOpaque(false);
        btnEye.setContentAreaFilled(false);
        btnEye.setBorderPainted(false);
        btnEye.setFocusPainted(false);
        btnEye.setPreferredSize(new Dimension(38, 38));
        btnEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEye.setToolTipText("Show password");

        btnEye.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                txtPassword.setEchoChar((char) 0);
                btnEye.setToolTipText("Hide password");
            } else {
                txtPassword.setEchoChar(defaultEchoChar);
                btnEye.setToolTipText("Show password");
            }
            btnEye.repaint();
            txtPassword.requestFocusInWindow();
        });

        // Focus styling on password box
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                passBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.PRIMARY, 1),
                        new EmptyBorder(0, 12, 0, 4)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                passBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                        new EmptyBorder(0, 12, 0, 4)
                ));
            }
        });

        passBox.add(txtPassword, BorderLayout.CENTER);
        passBox.add(btnEye, BorderLayout.EAST);

        passRow.add(lblPass, BorderLayout.NORTH);
        passRow.add(passBox, BorderLayout.CENTER);

        // Options Row (Remember Me + Forgot Password)
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setPreferredSize(new Dimension(360, 28));
        optionsPanel.setMaximumSize(new Dimension(360, 28));
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        chkRememberMe = new JCheckBox("Remember Me");
        chkRememberMe.setSelected(true);
        chkRememberMe.setFont(AppTheme.FONT_SMALL);
        chkRememberMe.setForeground(AppTheme.TEXT_MUTED);
        chkRememberMe.setOpaque(false);

        JLabel lblForgot = new JLabel("Forgot Password?");
        lblForgot.setFont(AppTheme.FONT_SMALL);
        lblForgot.setForeground(AppTheme.PRIMARY);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordDialog(LoginFrame.this).setVisible(true);
            }
        });

        optionsPanel.add(chkRememberMe, BorderLayout.WEST);
        optionsPanel.add(lblForgot, BorderLayout.EAST);

        // Login Button
        btnLogin = new RoundButton("Sign In", RoundButton.ButtonStyle.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(360, 44));
        btnLogin.setMaximumSize(new Dimension(360, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Press Enter to submit
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        // Assemble Inner Form
        innerForm.add(welcomeLabel);
        innerForm.add(Box.createVerticalStrut(6));
        innerForm.add(subtitleLabel);
        innerForm.add(Box.createVerticalStrut(14));
        innerForm.add(lblError);
        innerForm.add(Box.createVerticalStrut(8));
        innerForm.add(userRow);
        innerForm.add(Box.createVerticalStrut(12));
        innerForm.add(passRow);
        innerForm.add(Box.createVerticalStrut(12));
        innerForm.add(optionsPanel);
        innerForm.add(Box.createVerticalStrut(20));
        innerForm.add(btnLogin);

        formContainer.add(innerForm);
        return formContainer;
    }

    private void performLogin() {
        lblError.setText(" ");
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        try {
            User user = authController.login(username, password);
            if (user != null) {
                dispose();
                new com.vetranest.view.dashboard.MainDashboardFrame().setVisible(true);
            } else {
                lblError.setText("Invalid username or password. Please try again.");
            }
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
        }
    }
}
