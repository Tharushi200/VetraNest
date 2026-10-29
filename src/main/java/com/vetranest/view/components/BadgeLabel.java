package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern pill-shaped badge component for Status rendering (Pending, Confirmed, Completed, etc.).
 */
public class BadgeLabel extends JLabel {

    private Color bgColor;
    private Color textColor;

    public BadgeLabel(String text, String statusType) {
        super(text, SwingConstants.CENTER);
        setStatus(statusType);
        setFont(AppTheme.FONT_TAG);
        setOpaque(false);
        setBorder(new EmptyBorder(4, 12, 4, 12));
    }

    public void setStatus(String status) {
        if (status == null) status = "";
        setText(status);

        switch (status.toUpperCase()) {
            case "CONFIRMED":
            case "PAID":
            case "ACTIVE":
                bgColor = new Color(0xD1, 0xFA, 0xE5); // Mint light #D1FAE5
                textColor = new Color(0x06, 0x5F, 0x46); // Emerald dark
                break;
            case "PENDING":
                bgColor = new Color(0xFE, 0xF3, 0xC7); // Amber light #FEF3C7
                textColor = new Color(0x92, 0x40, 0x0E); // Amber dark
                break;
            case "COMPLETED":
                bgColor = new Color(0xEE, 0xF2, 0xFF); // Indigo light #EEF2FF
                textColor = new Color(0x37, 0x30, 0xA3); // Indigo dark
                break;
            case "CANCELLED":
            case "INACTIVE":
                bgColor = new Color(0xFE, 0xE2, 0xE2); // Red light
                textColor = new Color(0x99, 0x1B, 0x1B); // Red dark
                break;
            default:
                bgColor = new Color(0xF1, 0xF5, 0xF9);
                textColor = AppTheme.TEXT_PRIMARY;
                break;
        }
        setForeground(textColor);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw pill background
        g2.setColor(bgColor != null ? bgColor : new Color(0xF1, 0xF5, 0xF9));
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));

        g2.dispose();
        super.paintComponent(g);
    }
}
