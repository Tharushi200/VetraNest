package com.vetranest.view.customer;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.CustomerController;
import com.vetranest.model.Customer;
import com.vetranest.view.components.ModernCard;
import com.vetranest.view.components.ModernTable;
import com.vetranest.view.components.RoundButton;
import com.vetranest.view.components.SearchBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer Management View Panel.
 * Features live search, data table, edit/delete actions, and customer dialog integration.
 */
public class CustomerPanel extends JPanel {

    private final CustomerController controller;
    private final Frame parentFrame;

    private SearchBar searchBar;
    private DefaultTableModel tableModel;
    private ModernTable table;
    private List<Customer> currentCustomerList = new ArrayList<>();

    public CustomerPanel(Frame parent) {
        this.parentFrame = parent;
        this.controller = new CustomerController();
        initUI();
        loadCustomers();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_APP);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Action Bar (Title, Search, + Add Customer)
        add(createTopActionBar(), BorderLayout.NORTH);

        // 2. Main Table Container
        add(createTableCard(), BorderLayout.CENTER);
    }

    private JPanel createTopActionBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(AppTheme.FONT_HEADER);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        // Right side: SearchBar + Add Button
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        controls.setOpaque(false);

        searchBar = new SearchBar("Search by name, email or phone...");
        searchBar.setPreferredSize(new Dimension(280, 40));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(searchBar.getText());
            }
        });

        RoundButton btnAdd = new RoundButton("+ Add Customer", RoundButton.ButtonStyle.PRIMARY);
        btnAdd.setPreferredSize(new Dimension(150, 40));
        btnAdd.addActionListener(e -> openCustomerDialog(null));

        controls.add(searchBar);
        controls.add(btnAdd);

        bar.add(titleLabel, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        return bar;
    }

    private ModernCard createTableCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Double click row to edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selected = table.getSelectedRow();
                    if (selected >= 0 && selected < currentCustomerList.size()) {
                        openCustomerDialog(currentCustomerList.get(selected));
                    }
                }
            }
        });

        JScrollPane scrollPane = table.createScrollPane();

        // Footer Toolbar: Action Buttons (Edit, Delete) + Pagination (Matching Mockup)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBtns.setOpaque(false);

        RoundButton btnEdit = new RoundButton("✏️ Edit", RoundButton.ButtonStyle.OUTLINE);
        btnEdit.setPreferredSize(new Dimension(95, 32));
        btnEdit.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0 && selected < currentCustomerList.size()) {
                openCustomerDialog(currentCustomerList.get(selected));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        RoundButton btnDelete = new RoundButton("🗑️ Delete", RoundButton.ButtonStyle.DANGER);
        btnDelete.setPreferredSize(new Dimension(105, 32));
        btnDelete.addActionListener(e -> performDelete());

        actionBtns.add(btnEdit);
        actionBtns.add(btnDelete);

        // Pagination Pills
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        paginationPanel.setOpaque(false);
        paginationPanel.add(createPageButton("1", true));
        paginationPanel.add(createPageButton("2", false));
        paginationPanel.add(createPageButton("3", false));
        paginationPanel.add(createPageButton(">", false));

        footer.add(actionBtns, BorderLayout.WEST);
        footer.add(paginationPanel, BorderLayout.EAST);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JButton createPageButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(AppTheme.FONT_TAG);
        btn.setPreferredSize(new Dimension(32, 30));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBackground(AppTheme.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder());
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(AppTheme.TEXT_MUTED);
            btn.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1));
        }
        return btn;
    }

    public void loadCustomers() {
        try {
            currentCustomerList = controller.getAllCustomers();
            populateTable(currentCustomerList);
        } catch (Exception e) {
            System.err.println("[CustomerPanel] Error loading customers: " + e.getMessage());
        }
    }

    private void performSearch(String keyword) {
        try {
            currentCustomerList = controller.searchCustomers(keyword);
            populateTable(currentCustomerList);
        } catch (Exception e) {
            System.err.println("[CustomerPanel] Search error: " + e.getMessage());
        }
    }

    private void populateTable(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getCustomerCode(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress(),
                    "✏️ Edit  |  🗑️ Delete"
            });
        }
    }

    private void openCustomerDialog(Customer customer) {
        CustomerDialog dialog = new CustomerDialog(parentFrame, customer, this::loadCustomers);
        dialog.setVisible(true);
    }

    private void performDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= currentCustomerList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer c = currentCustomerList.get(selected);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer '" + c.getName() + "' (" + c.getCustomerCode() + ")?\n" +
                "All associated pet records will also be removed.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = controller.deleteCustomer(c.getCustomerId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomers();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
