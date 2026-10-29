package com.vetranest.view.dashboard;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AuthController;
import com.vetranest.model.User;
import com.vetranest.util.ImageUtil;
import com.vetranest.view.auth.LoginFrame;
import com.vetranest.view.appointment.AppointmentPanel;
import com.vetranest.view.customer.CustomerPanel;
import com.vetranest.view.pet.PetPanel;
import com.vetranest.view.report.ReportPanel;
import com.vetranest.view.treatment.TreatmentPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Application Shell Frame with Deep Navy Sidebar and CardLayout Content Area.
 */
public class MainDashboardFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentContainer;
    private JLabel headerTitleLabel;

    private final Map<String, JPanel> navButtons = new HashMap<>();
    private String currentTab = "DASHBOARD";

    private DashboardHomePanel dashboardHomePanel;

    public MainDashboardFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("VetraNest - Veterinary Management System");
        setSize(1240, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setBackground(AppTheme.BG_APP);

        // 1. Left Sidebar
        mainLayout.add(createSidebarPanel(), BorderLayout.WEST);

        // 2. Right Workspace (Header + Content Panel)
        JPanel rightWorkspace = new JPanel(new BorderLayout());
        rightWorkspace.setOpaque(false);
        rightWorkspace.add(createHeaderPanel(), BorderLayout.NORTH);

        // CardLayout Center Content
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setOpaque(false);

        // Screens in CardLayout
        dashboardHomePanel = new DashboardHomePanel(() -> switchTab("APPOINTMENTS"));
        contentContainer.add(dashboardHomePanel, "DASHBOARD");

        CustomerPanel customerPanel = new CustomerPanel(this);
        contentContainer.add(customerPanel, "CUSTOMERS");

        PetPanel petPanel = new PetPanel(this);
        contentContainer.add(petPanel, "PETS");

        AppointmentPanel appointmentPanel = new AppointmentPanel(this);
        contentContainer.add(appointmentPanel, "APPOINTMENTS");

        TreatmentPanel treatmentPanel = new TreatmentPanel(this);
        contentContainer.add(treatmentPanel, "TREATMENTS");

        ReportPanel reportPanel = new ReportPanel();
        contentContainer.add(reportPanel, "REPORTS");

        rightWorkspace.add(contentContainer, BorderLayout.CENTER);
        mainLayout.add(rightWorkspace, BorderLayout.CENTER);

        setContentPane(mainLayout);
        highlightNavButton("DASHBOARD");
    }

    /**
     * Deep Navy Sidebar with Branding and Navigation Items.
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(AppTheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 800));
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        // ── NORTH SECTION: Brand logo + title + role badge (fixed height) ──
        JPanel northSection = new JPanel();
        northSection.setLayout(new BoxLayout(northSection, BoxLayout.Y_AXIS));
        northSection.setOpaque(false);

        // Logo + app name row
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);
        ImageIcon logoIcon = ImageUtil.loadScaledIcon("assets/logo.png", 42, 42);
        JLabel logoLabel = new JLabel(logoIcon);
        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);
        JLabel brandName = new JLabel("VetraNest");
        brandName.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 18));
        brandName.setForeground(Color.WHITE);
        JLabel brandTag = new JLabel("Pet Care System");
        brandTag.setFont(AppTheme.FONT_SMALL);
        brandTag.setForeground(new Color(0x94, 0xA3, 0xB8));
        brandText.add(brandName);
        brandText.add(brandTag);
        brandRow.add(logoLabel);
        brandRow.add(brandText);
        northSection.add(brandRow);
        northSection.add(Box.createVerticalStrut(10));

        // Role badge pill
        User currentUser = AuthController.getCurrentUser();
        String role = currentUser != null ? currentUser.getRole() : "Admin";
        boolean isAdmin = "Admin".equalsIgnoreCase(role);
        JLabel roleBadge = new JLabel("  \u25CF  " + role.toUpperCase() + "  ");
        roleBadge.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 10));
        roleBadge.setForeground(isAdmin ? new Color(0xA5, 0xB4, 0xFC) : new Color(0x6E, 0xE7, 0xB7));
        roleBadge.setBackground(isAdmin ? new Color(0x2D, 0x28, 0x60) : new Color(0x06, 0x40, 0x2E));
        roleBadge.setOpaque(true);
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isAdmin ? new Color(0x6C, 0x63, 0xFF, 80) : new Color(0x10, 0xB9, 0x81, 80), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(roleBadge);
        northSection.add(badgeRow);
        northSection.add(Box.createVerticalStrut(18));

        // ── NAV SECTION: Only nav items (no expandable wrappers) ──
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        menuPanel.add(createNavItem("DASHBOARD",    "\uD83C\uDFE0", "Dashboard",    false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavItem("CUSTOMERS",    "\uD83D\uDC65", "Customers",    false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavItem("PETS",         "\uD83D\uDC3E", "Pets",         false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavItem("APPOINTMENTS", "\uD83D\uDCC5", "Appointments", false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavItem("TREATMENTS",   "\uD83D\uDC8A", "Treatments",   false));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavItem("REPORTS",      "\uD83D\uDCCA", "Reports",      false));

        // northSection is NORTH (fixed height), menuPanel is CENTER (fills remaining)
        JPanel topColumn = new JPanel(new BorderLayout());
        topColumn.setOpaque(false);
        topColumn.add(northSection, BorderLayout.NORTH);
        topColumn.add(menuPanel,    BorderLayout.CENTER);

        // ── SOUTH: Logout pinned to sidebar bottom ──
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        JPanel logoutItem = createNavItem("LOGOUT", "\uD83D\uDEAA", "Logout", false);
        bottomPanel.add(logoutItem, BorderLayout.SOUTH);

        sidebar.add(topColumn,   BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createNavItem(String key, String iconEmoji, String labelText, boolean locked) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 9)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (key.equals(currentTab)) {
                    g2.setColor(AppTheme.PRIMARY);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        item.setOpaque(false);
        item.setMaximumSize(new Dimension(200, 42));
        item.setPreferredSize(new Dimension(200, 42));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon label — uses Segoe UI Emoji font so emoji render correctly on Windows
        JLabel iconLabel = new JLabel(iconEmoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        iconLabel.setForeground(locked ? new Color(0x4A, 0x55, 0x68) : new Color(0xCB, 0xD5, 0xE1));

        // Text label — uses regular Segoe UI for crisp text
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(AppTheme.FONT_BODY_BOLD);
        textLabel.setForeground(locked ? new Color(0x4A, 0x55, 0x68) : new Color(0xCB, 0xD5, 0xE1));

        item.add(iconLabel);
        item.add(textLabel);
        navButtons.put(key, item);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ("LOGOUT".equals(key)) {
                    performLogout();
                } else {
                    switchTab(key);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!key.equals(currentTab)) {
                    item.setBackground(AppTheme.SIDEBAR_HOVER);
                    item.setOpaque(true);
                    item.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!key.equals(currentTab)) {
                    item.setOpaque(false);
                    item.repaint();
                }
            }
        });

        return item;
    }

    public void switchTab(String tabKey) {
        this.currentTab = tabKey;
        cardLayout.show(contentContainer, tabKey);
        highlightNavButton(tabKey);

        // Update Header Title
        if (headerTitleLabel != null) {
            String titleText = tabKey.substring(0, 1).toUpperCase() + tabKey.substring(1).toLowerCase();
            if ("CUSTOMERS".equals(tabKey)) titleText = "Customer Management";
            if ("PETS".equals(tabKey)) titleText = "Pet Profiles";
            if ("APPOINTMENTS".equals(tabKey)) titleText = "Appointments Management";
            if ("TREATMENTS".equals(tabKey)) titleText = "Medical & Treatment Records";
            if ("REPORTS".equals(tabKey)) titleText = "JasperReports Analytics";
            headerTitleLabel.setText(titleText);
        }

        if ("DASHBOARD".equals(tabKey) && dashboardHomePanel != null) {
            dashboardHomePanel.refreshData();
        }
    }

    private void highlightNavButton(String tabKey) {
        for (Map.Entry<String, JPanel> entry : navButtons.entrySet()) {
            JPanel p = entry.getValue();
            // Each nav item now has 2 components: iconLabel (index 0) + textLabel (index 1)
            Color activeColor = Color.WHITE;
            Color inactiveColor = new Color(0xCB, 0xD5, 0xE1);
            boolean isActive = entry.getKey().equals(tabKey);
            Color fg = isActive ? activeColor : inactiveColor;
            for (Component comp : p.getComponents()) {
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setForeground(fg);
                }
            }
            p.setOpaque(false);
            p.repaint();
        }
    }

    /**
     * Top Header Panel with Title, Date, and User Profile Badge.
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();

                // Harmonious pastel lavender-to-mint gradient matching brand identity
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0xF1, 0xEE, 0xFF),     // Soft pastel lavender
                        w, 0, new Color(0xE8, 0xF8, 0xF2)      // Fresh pastel mint
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Subtle bottom border separating header from content
                g2.setColor(new Color(0xD6, 0xDC, 0xEB));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(0, h - 1, w, h - 1);

                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 24, 12, 24));

        // Screen Title (Deep Navy matching sidebar branding)
        headerTitleLabel = new JLabel("Dashboard");
        headerTitleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 20));
        headerTitleLabel.setForeground(new Color(0x1E, 0x1B, 0x4B));

        // Right side: Date + User Profile Chip
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightInfo.setOpaque(false);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy"));
        JPanel dateChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        dateChip.setBackground(Color.WHITE);
        dateChip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2, 0xE8, 0xF0), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        JLabel dateIconLabel = new JLabel("\uD83D\uDCC5");
        dateIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        dateIconLabel.setForeground(AppTheme.TEXT_MUTED);
        JLabel dateLabel = new JLabel(today);
        dateLabel.setFont(AppTheme.FONT_BODY);
        dateLabel.setForeground(AppTheme.TEXT_MUTED);
        dateChip.add(dateIconLabel);
        dateChip.add(dateLabel);

        User current = AuthController.getCurrentUser();
        String displayName = current != null ? current.getFullName() : "Administrator";
        String displayRole = current != null ? current.getRole() : "Admin";
        boolean isAdmin = "Admin".equalsIgnoreCase(displayRole);

        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        userChip.setBackground(Color.WHITE);
        userChip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isAdmin ? new Color(0xC7, 0xD2, 0xFE) : new Color(0xA7, 0xF3, 0xD0), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));

        JLabel avatar = new JLabel("\uD83D\uDC64");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        avatar.setForeground(isAdmin ? AppTheme.PRIMARY : new Color(0x05, 0x96, 0x69));

        JLabel userName = new JLabel(displayName + " (" + displayRole + ")");
        userName.setFont(AppTheme.FONT_BODY_BOLD);
        userName.setForeground(isAdmin ? new Color(0x37, 0x30, 0xA3) : new Color(0x06, 0x5F, 0x46));

        userChip.add(avatar);
        userChip.add(userName);

        rightInfo.add(dateChip);
        rightInfo.add(userChip);

        header.add(headerTitleLabel, BorderLayout.WEST);
        header.add(rightInfo, BorderLayout.EAST);

        return header;
    }


    private void performLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of VetraNest?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            AuthController.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public void registerPanel(String key, JPanel panel) {
        contentContainer.add(panel, key);
    }
}
