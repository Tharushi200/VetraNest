package com.vetranest.view.treatment;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.TreatmentController;
import com.vetranest.model.Treatment;
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
 * Treatment & Prescription Management View Panel.
 * Displays diagnosis history, prescribed medications, instructions, and attending doctors.
 */
public class TreatmentPanel extends JPanel {

    private final TreatmentController controller;
    private final Frame parentFrame;

    private SearchBar searchBar;
    private DefaultTableModel tableModel;
    private ModernTable table;
    private List<Treatment> currentList = new ArrayList<>();

    public TreatmentPanel(Frame parent) {
        this.parentFrame = parent;
        this.controller = new TreatmentController();
        initUI();
        loadTreatments();
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

        JLabel titleLabel = new JLabel("Clinical Treatments & Prescriptions");
        titleLabel.setFont(AppTheme.FONT_HEADER);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        searchBar = new SearchBar("Search diagnosis, pet, medication...");
        searchBar.setPreferredSize(new Dimension(280, 40));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        RoundButton btnAdd = new RoundButton("+ New Treatment", RoundButton.ButtonStyle.PRIMARY);
        btnAdd.setPreferredSize(new Dimension(160, 40));
        btnAdd.addActionListener(e -> openTreatmentDialog(null));

        controls.add(searchBar);
        controls.add(btnAdd);

        bar.add(titleLabel, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        return bar;
    }

    private ModernCard createTableCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        String[] columns = {"ID", "Date", "Pet Name", "Species", "Owner", "Diagnosis", "Medication", "Doctor", "Actions"};
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
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && row < currentList.size()) {
                    Treatment t = currentList.get(row);
                    if (col == 8) {
                        showActionPopup(e.getComponent(), e.getX(), e.getY(), t);
                    } else if (e.getClickCount() == 2) {
                        openTreatmentDialog(t);
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

    private void showActionPopup(Component invoker, int x, int y, Treatment t) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemView = new JMenuItem("📋 View Details / Prescription");
        itemView.addActionListener(e -> showDetailDialog(t));
        menu.add(itemView);

        JMenuItem itemEdit = new JMenuItem("✏ Edit Treatment");
        itemEdit.addActionListener(e -> openTreatmentDialog(t));
        menu.add(itemEdit);

        menu.addSeparator();

        JMenuItem itemDelete = new JMenuItem("🗑 Delete Record");
        itemDelete.setForeground(new Color(0xDC2626));
        itemDelete.addActionListener(e -> deleteTreatment(t));
        menu.add(itemDelete);

        menu.show(invoker, x, y);
    }

    private void showDetailDialog(Treatment t) {
        JDialog dlg = new JDialog(parentFrame, "Clinical Record #" + t.getTreatmentId(), true);
        dlg.setSize(440, 480);
        dlg.setLocationRelativeTo(parentFrame);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppTheme.BG_CARD);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        p.add(makeField("Pet Name:", t.getPetName() + " (" + t.getPetSpecies() + ")"));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Owner:", t.getOwnerName() != null ? t.getOwnerName() : "N/A"));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Treatment Date:", t.getTreatmentDate() != null ? t.getTreatmentDate().toString() : ""));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Attending Vet:", t.getDoctorName()));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Diagnosis:", t.getDiagnosis()));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Medication:", t.getMedication()));
        p.add(Box.createVerticalStrut(8));
        p.add(makeField("Instructions:", t.getInstructions() != null && !t.getInstructions().isEmpty() ? t.getInstructions() : "None"));
        p.add(Box.createVerticalStrut(16));

        RoundButton btnClose = new RoundButton("Close", RoundButton.ButtonStyle.PRIMARY);
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.addActionListener(ev -> dlg.dispose());
        p.add(btnClose);

        dlg.getContentPane().add(p);
        dlg.setVisible(true);
    }

    private JPanel makeField(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(120, 24));

        JLabel val = new JLabel("<html><b>" + value + "</b></html>");
        val.setFont(AppTheme.FONT_BODY);
        val.setForeground(AppTheme.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private void deleteTreatment(Treatment t) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete treatment record #" + t.getTreatmentId() + " for " + t.getPetName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = controller.deleteTreatment(t.getTreatmentId());
                if (ok) {
                    loadTreatments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete treatment record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadTreatments() {
        try {
            currentList = controller.getAllTreatments();
            populateTable(currentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String keyword = searchBar.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadTreatments();
            return;
        }

        try {
            List<Treatment> all = controller.getAllTreatments();
            List<Treatment> filtered = new ArrayList<>();
            for (Treatment t : all) {
                if ((t.getPetName() != null && t.getPetName().toLowerCase().contains(keyword)) ||
                    (t.getOwnerName() != null && t.getOwnerName().toLowerCase().contains(keyword)) ||
                    (t.getDiagnosis() != null && t.getDiagnosis().toLowerCase().contains(keyword)) ||
                    (t.getMedication() != null && t.getMedication().toLowerCase().contains(keyword)) ||
                    (t.getDoctorName() != null && t.getDoctorName().toLowerCase().contains(keyword))) {
                    filtered.add(t);
                }
            }
            currentList = filtered;
            populateTable(currentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateTable(List<Treatment> list) {
        tableModel.setRowCount(0);
        for (Treatment t : list) {
            tableModel.addRow(new Object[]{
                    "#" + t.getTreatmentId(),
                    t.getTreatmentDate() != null ? t.getTreatmentDate().toString() : "-",
                    t.getPetName() != null ? t.getPetName() : "Pet #" + t.getPetId(),
                    t.getPetSpecies() != null ? t.getPetSpecies() : "-",
                    t.getOwnerName() != null ? t.getOwnerName() : "-",
                    t.getDiagnosis(),
                    t.getMedication(),
                    t.getDoctorName(),
                    "••• Action"
            });
        }
    }

    private void openTreatmentDialog(Treatment editTreatment) {
        AddTreatmentDialog dialog = new AddTreatmentDialog(
                parentFrame,
                editTreatment != null ? editTreatment.getPetId() : 0,
                editTreatment != null ? editTreatment.getAppointmentId() : 0,
                editTreatment,
                this::loadTreatments
        );
        dialog.setVisible(true);
    }
}
