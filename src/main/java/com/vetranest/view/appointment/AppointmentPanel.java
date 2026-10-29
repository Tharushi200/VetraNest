package com.vetranest.view.appointment;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AppointmentController;
import com.vetranest.model.Appointment;
import com.vetranest.view.components.ModernCard;
import com.vetranest.view.components.ModernTable;
import com.vetranest.view.components.RoundButton;
import com.vetranest.view.components.SearchBar;
import com.vetranest.view.treatment.AddTreatmentDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment Management View Panel (Transaction UI).
 * Allows booking clinic visits, preventing slot conflicts, updating status,
 * and transitioning visits into Clinical Treatment records.
 */
public class AppointmentPanel extends JPanel {

    private final AppointmentController controller;
    private final Frame parentFrame;

    private SearchBar searchBar;
    private JComboBox<String> cmbStatusFilter;
    private DefaultTableModel tableModel;
    private ModernTable table;
    private List<Appointment> currentList = new ArrayList<>();

    public AppointmentPanel(Frame parent) {
        this.parentFrame = parent;
        this.controller = new AppointmentController();
        initUI();
        loadAppointments();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_APP);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Action Bar
        add(createTopActionBar(), BorderLayout.NORTH);

        // 2. Table Card
        add(createTableCard(), BorderLayout.CENTER);
    }

    private JPanel createTopActionBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);

        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(AppTheme.FONT_HEADER);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        // Status Filter
        cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "Pending", "Confirmed", "Completed", "Cancelled"});
        cmbStatusFilter.setFont(AppTheme.FONT_BODY);
        cmbStatusFilter.setPreferredSize(new Dimension(140, 40));
        cmbStatusFilter.addActionListener(e -> performSearch());

        // Live Search Bar
        searchBar = new SearchBar("Search customer, pet, or service...");
        searchBar.setPreferredSize(new Dimension(260, 40));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        RoundButton btnBook = new RoundButton("+ Book Appointment", RoundButton.ButtonStyle.PRIMARY);
        btnBook.setPreferredSize(new Dimension(180, 40));
        btnBook.addActionListener(e -> openBookDialog(null));

        controls.add(searchBar);
        controls.add(cmbStatusFilter);
        controls.add(btnBook);

        bar.add(titleLabel, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        return bar;
    }

    private ModernCard createTableCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        String[] columns = {"ID", "Date", "Slot", "Customer", "Pet", "Service", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.setRowHeight(44);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(95);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);

        // Status color renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String status = val != null ? val.toString() : "";
                if ("Confirmed".equalsIgnoreCase(status)) {
                    l.setForeground(new Color(0x0284C7));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else if ("Completed".equalsIgnoreCase(status)) {
                    l.setForeground(new Color(0x16A34A));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else if ("Cancelled".equalsIgnoreCase(status)) {
                    l.setForeground(new Color(0xDC2626));
                } else {
                    l.setForeground(new Color(0xD97706));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                }
                return l;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && row < currentList.size()) {
                    Appointment app = currentList.get(row);
                    if (col == 7) {
                        showActionPopup(e.getComponent(), e.getX(), e.getY(), app);
                    } else if (e.getClickCount() == 2) {
                        openBookDialog(app);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppTheme.BG_CARD);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void showActionPopup(Component invoker, int x, int y, Appointment app) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemEdit = new JMenuItem("✏ Edit Appointment");
        itemEdit.addActionListener(e -> openBookDialog(app));
        menu.add(itemEdit);

        if (!"Confirmed".equalsIgnoreCase(app.getStatus())) {
            JMenuItem itemConfirm = new JMenuItem("✔ Mark Confirmed");
            itemConfirm.addActionListener(e -> updateStatus(app, "Confirmed"));
            menu.add(itemConfirm);
        }

        if (!"Completed".equalsIgnoreCase(app.getStatus())) {
            JMenuItem itemComplete = new JMenuItem("🎉 Mark Completed");
            itemComplete.addActionListener(e -> updateStatus(app, "Completed"));
            menu.add(itemComplete);
        }

        JMenuItem itemTreat = new JMenuItem("💊 Add Treatment Record");
        itemTreat.addActionListener(e -> {
            AddTreatmentDialog dlg = new AddTreatmentDialog(parentFrame, app.getPetId(), app.getAppointmentId(), () -> {});
            dlg.setVisible(true);
        });
        menu.add(itemTreat);

        menu.addSeparator();

        if (!"Cancelled".equalsIgnoreCase(app.getStatus())) {
            JMenuItem itemCancel = new JMenuItem("❌ Cancel Appointment");
            itemCancel.setForeground(new Color(0xDC2626));
            itemCancel.addActionListener(e -> updateStatus(app, "Cancelled"));
            menu.add(itemCancel);
        }

        JMenuItem itemDelete = new JMenuItem("🗑 Delete Appointment");
        itemDelete.setForeground(new Color(0xDC2626));
        itemDelete.addActionListener(e -> deleteAppointment(app));
        menu.add(itemDelete);

        menu.show(invoker, x, y);
    }

    private void updateStatus(Appointment app, String newStatus) {
        try {
            boolean ok = controller.updateStatus(app.getAppointmentId(), newStatus);
            if (ok) {
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment(Appointment app) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete appointment #" + app.getAppointmentId() + " (" + app.getPetName() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = controller.deleteAppointment(app.getAppointmentId());
                if (ok) {
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadAppointments() {
        try {
            currentList = controller.getAllAppointments();
            populateTable(currentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String keyword = searchBar.getText().trim();
        String selectedStatus = (String) cmbStatusFilter.getSelectedItem();

        try {
            List<Appointment> all;
            if (keyword.isEmpty()) {
                all = controller.getAllAppointments();
            } else {
                all = controller.searchAppointments(keyword);
            }

            if (selectedStatus != null && !"All Statuses".equalsIgnoreCase(selectedStatus)) {
                List<Appointment> filtered = new ArrayList<>();
                for (Appointment a : all) {
                    if (selectedStatus.equalsIgnoreCase(a.getStatus())) {
                        filtered.add(a);
                    }
                }
                currentList = filtered;
            } else {
                currentList = all;
            }

            populateTable(currentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateTable(List<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                    "#" + a.getAppointmentId(),
                    a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "-",
                    a.getTimeSlot(),
                    a.getCustomerName() != null ? a.getCustomerName() : "ID: " + a.getCustomerId(),
                    a.getPetName() != null ? a.getPetName() : "ID: " + a.getPetId(),
                    a.getServiceType(),
                    a.getStatus(),
                    "••• Action"
            });
        }
    }

    private void openBookDialog(Appointment editApp) {
        BookAppointmentDialog dialog;
        if (editApp == null) {
            dialog = new BookAppointmentDialog(parentFrame, this::loadAppointments);
        } else {
            dialog = new BookAppointmentDialog(
                    parentFrame,
                    this::loadAppointments,
                    editApp.getAppointmentId(),
                    editApp.getAppointmentDate() != null ? editApp.getAppointmentDate().toString() : "",
                    editApp.getTimeSlot(),
                    editApp.getServiceType(),
                    editApp.getStatus(),
                    editApp.getCustomerId(),
                    editApp.getPetId()
            );
        }
        dialog.setVisible(true);
    }
}
