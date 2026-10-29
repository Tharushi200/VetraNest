package com.vetranest.view.auth;

import com.vetranest.config.AppTheme;
import com.vetranest.config.DatabaseConnection;
import com.vetranest.config.DatabaseInitializer;
import com.vetranest.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern animated Splash Screen for VetraNest.
 * Displays logo, clinic branding, and an animated progress bar during startup.
 */
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel percentLabel;

    public SplashScreen() {
        initUI();
    }

    private void initUI() {
        setSize(520, 360);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0)); // Transparent window for rounded effect

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw dark gradient background matching the brand
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x1E, 0x1B, 0x4B),
                        w, h, new Color(0x2D, 0x27, 0x6E)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));

                // Soft outer border
                g2.setColor(new Color(0x6C, 0x63, 0xFF, 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 24, 24));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Logo
        ImageIcon logoIcon = ImageUtil.loadScaledIcon("assets/logo.png", 90, 90);
        JLabel logoLabel = new JLabel(logoIcon != null ? logoIcon : new JLabel("🐾").getIcon());
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // App Title
        JLabel titleLabel = new JLabel("VetraNest");
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle / Tagline
        JLabel subtitleLabel = new JLabel("Veterinary Management System  •  Healthy Pets ❤️ Happy Lives");
        subtitleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0x94, 0xA3, 0xB8)); // Cool slate
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Status & Percentage Row
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(440, 25));

        statusLabel = new JLabel("Starting VetraNest...");
        statusLabel.setFont(AppTheme.FONT_SMALL);
        statusLabel.setForeground(new Color(0xCB, 0xD5, 0xE1));

        percentLabel = new JLabel("0%");
        percentLabel.setFont(AppTheme.FONT_BODY_BOLD);
        percentLabel.setForeground(AppTheme.SECONDARY);

        infoPanel.add(statusLabel, BorderLayout.WEST);
        infoPanel.add(percentLabel, BorderLayout.EAST);

        // Modern Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(440, 8));
        progressBar.setMaximumSize(new Dimension(440, 8));
        progressBar.setForeground(AppTheme.SECONDARY);
        progressBar.setBackground(new Color(0x33, 0x2E, 0x6E));
        progressBar.setBorderPainted(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble Layout
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(progressBar);

        setContentPane(mainPanel);
    }

    /**
     * Starts the loading sequence in a background worker.
     */
    public void startLoading(Runnable onComplete) {
        setVisible(true);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Step 1: UI Engine
                updateStatus("Initializing modern UI engine...", 15);
                Thread.sleep(300);

                // Step 2: Database Connection
                updateStatus("Connecting to MySQL Database...", 40);
                DatabaseConnection.getInstance().testConnection();
                Thread.sleep(300);

                // Step 3: Schema & Data verification
                updateStatus("Verifying database schema & tables...", 70);
                DatabaseInitializer.initializeDatabase();
                Thread.sleep(300);

                // Step 4: Assets & Security
                updateStatus("Loading assets & security context...", 90);
                Thread.sleep(200);

                // Step 5: Ready
                updateStatus("Welcome to VetraNest!", 100);
                Thread.sleep(400);

                return null;
            }

            private void updateStatus(String msg, int progress) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(msg);
                    percentLabel.setText(progress + "%");
                    progressBar.setValue(progress);
                });
            }

            @Override
            protected void done() {
                dispose();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        };

        worker.execute();
    }
}
