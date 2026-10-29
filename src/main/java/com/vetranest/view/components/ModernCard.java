package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Clean elevated white card container with rounded corners and soft borders.
 */
public class ModernCard extends JPanel {

    private int cornerRadius = 16;
    private Color borderColor = AppTheme.BORDER;

    public ModernCard() {
        this(new BorderLayout());
    }

    public ModernCard(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(16, 18, 16, 18));
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // White card background
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Subtle Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, width - 1, height - 1, cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g);
    }
}
