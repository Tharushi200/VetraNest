package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Metric Card Component for Dashboard.
 * Displays title, big numeric counter, icon and colored accent background.
 */
public class StatCard extends JPanel {

    private final String title;
    private final JLabel valueLabel;
    private final Color cardColor;
    private final String iconSymbol;

    public StatCard(String title, String initialValue, Color cardColor, String iconSymbol) {
        this.title = title;
        this.cardColor = cardColor;
        this.iconSymbol = iconSymbol;

        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setPreferredSize(new Dimension(230, 95));

        // Left Icon Panel (Circle Badge)
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(iconSymbol)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(iconSymbol, x, y);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(50, 50));

        // Right Text Panel (Title + Big Value)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.PLAIN, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 220));

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        valueLabel.setForeground(Color.WHITE);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);

        add(iconPanel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        this.valueLabel.setText(value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw rounded gradient card
        GradientPaint gp = new GradientPaint(
                0, 0, cardColor,
                width, height, cardColor.darker()
        );
        g2.setPaint(gp);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 16, 16));

        g2.dispose();
        super.paintComponent(g);
    }
}
