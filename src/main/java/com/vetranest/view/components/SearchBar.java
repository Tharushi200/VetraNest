package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern SearchBar component with magnifying glass icon and placeholder hint.
 */
public class SearchBar extends JTextField {

    private final String placeholder;
    private final int cornerRadius = 10;

    public SearchBar(String placeholder) {
        this.placeholder = placeholder;
        setFont(AppTheme.FONT_BODY);
        setForeground(AppTheme.TEXT_PRIMARY);
        setCaretColor(AppTheme.PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 36, 8, 14));
        setPreferredSize(new Dimension(280, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Draw border
        g2.setColor(hasFocus() ? AppTheme.PRIMARY : AppTheme.BORDER);
        g2.setStroke(new BasicStroke(hasFocus() ? 1.5f : 1.0f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, height - 1, cornerRadius, cornerRadius));

        // Draw search magnifying glass icon
        g2.setColor(AppTheme.TEXT_MUTED);
        int iconX = 12;
        int iconY = height / 2 - 6;
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(iconX, iconY, 8, 8);
        g2.drawLine(iconX + 7, iconY + 7, iconX + 12, iconY + 12);

        // Draw placeholder text if empty
        if (getText().isEmpty() && !hasFocus()) {
            g2.setFont(AppTheme.FONT_BODY);
            g2.setColor(AppTheme.TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 36, textY);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
