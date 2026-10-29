package com.vetranest.config;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

/**
 * VetraNest Centralized Theme & Styling System.
 * Manages modern FlatLaf configuration, color palette, typography and UI tokens.
 */
public class AppTheme {

    // Primary Brand Colors (from Reference Board)
    public static final Color PRIMARY = new Color(0x6C, 0x63, 0xFF);       // Royal Indigo #6C63FF
    public static final Color PRIMARY_HOVER = new Color(0x5A, 0x52, 0xE0); // Darker Indigo
    public static final Color SECONDARY = new Color(0x10, 0xB9, 0x81);     // Emerald / Medical Teal #10B981
    public static final Color SECONDARY_HOVER = new Color(0x05, 0x96, 0x69);
    public static final Color ACCENT_ORANGE = new Color(0xF5, 0x9E, 0x0B); // Amber / Gold #F59E0B
    public static final Color ACCENT_BLUE = new Color(0x3B, 0x82, 0xF6);   // Sky Blue #3B82F6
    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);        // Crimson #EF4444

    // Surface & Layout Backgrounds
    public static final Color BG_APP = new Color(0xF8, 0xFA, 0xFC);        // Light Slate #F8FAFC
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_SIDEBAR = new Color(0x1E, 0x1B, 0x4B);    // Deep Navy #1E1B4B
    public static final Color SIDEBAR_HOVER = new Color(0x2E, 0x28, 0x56);
    public static final Color SIDEBAR_ACTIVE = new Color(0x6C, 0x63, 0xFF);

    // Text & Border Colors
    public static final Color TEXT_PRIMARY = new Color(0x1F, 0x29, 0x37);  // Dark Slate #1F2937
    public static final Color TEXT_MUTED = new Color(0x6B, 0x72, 0x80);    // Cool Gray #6B7280
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color BORDER = new Color(0xE2, 0xE8, 0xF0);        // Slate border #E2E8F0
    public static final Color TABLE_HOVER = new Color(0xF1, 0xF5, 0xF9);
    public static final Color TABLE_SELECTION = new Color(0xEE, 0xF2, 0xFF);

    // Modern Typography
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_HEADER_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_TAG = new Font(FONT_FAMILY, Font.BOLD, 11);

    /**
     * Initializes FlatLaf Look and Feel with enterprise modern customizations.
     */
    public static void setupFlatLaf() {
        try {
            // Apply Modern FlatLightLaf
            FlatLightLaf.setup();

            // Set global modern UI arcs and metrics
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Component.focusWidth", 2);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Component.focusColor", new Color(0x6C, 0x63, 0xFF, 120));

            // ScrollBar Modern Styling
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

            // Table Modern Styling
            UIManager.put("Table.rowHeight", 42);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.gridColor", new Color(0xF1, 0xF5, 0xF9));
            UIManager.put("Table.selectionBackground", TABLE_SELECTION);
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("TableHeader.font", FONT_BODY_BOLD);
            UIManager.put("TableHeader.background", new Color(0xF8, 0xFA, 0xFC));
            UIManager.put("TableHeader.foreground", TEXT_MUTED);
            UIManager.put("TableHeader.height", 38);

        } catch (Exception ex) {
            System.err.println("[AppTheme] Failed to initialize FlatLaf theme: " + ex.getMessage());
        }
    }
}
