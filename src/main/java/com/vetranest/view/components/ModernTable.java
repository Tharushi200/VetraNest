package com.vetranest.view.components;

import com.vetranest.config.AppTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * ModernTable encapsulates a styled JTable with alternating row colors,
 * generous cell padding, status badge renderers, and modern FlatLaf aesthetics.
 */
public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(42);
        setShowGrid(false);
        setShowHorizontalLines(true);
        setGridColor(new Color(0xF1, 0xF5, 0xF9));
        setSelectionBackground(AppTheme.TABLE_SELECTION);
        setSelectionForeground(AppTheme.TEXT_PRIMARY);
        setFont(AppTheme.FONT_BODY);
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);

        // Header Style
        JTableHeader header = getTableHeader();
        header.setFont(AppTheme.FONT_BODY_BOLD);
        header.setBackground(new Color(0xF8, 0xFA, 0xFC));
        header.setForeground(AppTheme.TEXT_MUTED);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        // Default Cell Renderer with Padding & Alternating Rows
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 14, 0, 14));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xFB, 0xFC, 0xFD));
                    c.setForeground(AppTheme.TEXT_PRIMARY);
                }

                // If value represents a status string, use BadgeLabel
                if (value != null) {
                    String strVal = value.toString().trim();
                    if (strVal.equalsIgnoreCase("Pending") || strVal.equalsIgnoreCase("Confirmed") ||
                        strVal.equalsIgnoreCase("Completed") || strVal.equalsIgnoreCase("Paid") ||
                        strVal.equalsIgnoreCase("Cancelled")) {
                        return new BadgeLabel(strVal, strVal);
                    }
                }

                return c;
            }
        });
    }

    /**
     * Convenience method to wrap the ModernTable in a clean borderless JScrollPane.
     */
    public JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
}
