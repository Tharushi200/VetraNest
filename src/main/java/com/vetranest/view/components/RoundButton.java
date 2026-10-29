package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom modern rounded button with smooth hover effects and color variants.
 */
public class RoundButton extends JButton {

    public enum ButtonStyle {
        PRIMARY, SECONDARY, DANGER, OUTLINE, NEUTRAL
    }

    private ButtonStyle style = ButtonStyle.PRIMARY;
    private int cornerRadius = 12;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public RoundButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        initUI();
    }

    private void initUI() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        // Use Segoe UI Emoji to render both emoji icons and Latin text in button labels
        setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 20, 10, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
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

        Color bgColor;
        Color fgColor;
        Color borderColor = null;

        switch (style) {
            case SECONDARY:
                bgColor = isPressed ? AppTheme.SECONDARY_HOVER.darker() :
                          isHovered ? AppTheme.SECONDARY_HOVER : AppTheme.SECONDARY;
                fgColor = Color.WHITE;
                break;
            case DANGER:
                bgColor = isPressed ? AppTheme.DANGER.darker() :
                          isHovered ? AppTheme.DANGER.brighter() : AppTheme.DANGER;
                fgColor = Color.WHITE;
                break;
            case OUTLINE:
                bgColor = isHovered ? new Color(0x6C, 0x63, 0xFF, 20) : Color.WHITE;
                fgColor = AppTheme.PRIMARY;
                borderColor = AppTheme.PRIMARY;
                break;
            case NEUTRAL:
                bgColor = isHovered ? new Color(0xF1, 0xF5, 0xF9) : Color.WHITE;
                fgColor = AppTheme.TEXT_PRIMARY;
                borderColor = AppTheme.BORDER;
                break;
            case PRIMARY:
            default:
                bgColor = isPressed ? AppTheme.PRIMARY_HOVER.darker() :
                          isHovered ? AppTheme.PRIMARY_HOVER : AppTheme.PRIMARY;
                fgColor = Color.WHITE;
                break;
        }

        if (!isEnabled()) {
            bgColor = new Color(0xE2, 0xE8, 0xF0);
            fgColor = AppTheme.TEXT_MUTED;
            borderColor = null;
        }

        // Draw background
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));

        // Draw border if applicable
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));
        }

        // Draw text and icon
        setForeground(fgColor);
        super.paintComponent(g2);
        g2.dispose();
    }
}
