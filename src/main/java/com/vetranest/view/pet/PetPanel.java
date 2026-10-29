package com.vetranest.view.pet;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.PetController;
import com.vetranest.model.Pet;
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
 * Pet Management View Panel.
 * Displays pet profiles, species filters, owner relations, and CRUD controls.
 */
public class PetPanel extends JPanel {

    private final PetController controller;
    private final Frame parentFrame;

    private SearchBar searchBar;
    private JComboBox<String> cmbSpeciesFilter;
    private DefaultTableModel tableModel;
    private ModernTable table;
    private List<Pet> currentPetList = new ArrayList<>();

    public PetPanel(Frame parent) {
        this.parentFrame = parent;
        this.controller = new PetController();
        initUI();
        loadPets();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(AppTheme.BG_APP);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // 1. Top Action Bar (Title, Search, Species Filter, + Add Pet)
        add(createTopActionBar(), BorderLayout.NORTH);

        // 2. Main Table Container
        add(createTableCard(), BorderLayout.CENTER);
    }

    private JPanel createTopActionBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setOpaque(false);

        JLabel titleLabel = new JLabel("Pet Management");
        titleLabel.setFont(AppTheme.FONT_HEADER);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        // Species Filter
        cmbSpeciesFilter = new JComboBox<>(new String[]{"All Species", "Dog", "Cat", "Bird", "Rabbit", "Exotic"});
        cmbSpeciesFilter.setFont(AppTheme.FONT_BODY);
        cmbSpeciesFilter.setPreferredSize(new Dimension(130, 40));
        cmbSpeciesFilter.addActionListener(e -> performSearch());

        // Live Search Bar
        searchBar = new SearchBar("Search by pet name or owner...");
        searchBar.setPreferredSize(new Dimension(260, 40));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        RoundButton btnAdd = new RoundButton("+ Add Pet", RoundButton.ButtonStyle.PRIMARY);
        btnAdd.setPreferredSize(new Dimension(130, 40));
        btnAdd.addActionListener(e -> openPetDialog(null));

        controls.add(searchBar);
        controls.add(cmbSpeciesFilter);
        controls.add(btnAdd);

        bar.add(titleLabel, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        return bar;
    }

    private ModernCard createTableCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        String[] columns = {"ID", "Pet Name", "Species", "Breed", "Owner", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Double click row to edit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selected = table.getSelectedRow();
                    if (selected >= 0 && selected < currentPetList.size()) {
                        openPetDialog(currentPetList.get(selected));
                    }
                }
            }
        });

        JScrollPane scrollPane = table.createScrollPane();

        // Footer Actions & Pagination
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionBtns.setOpaque(false);

        RoundButton btnEdit = new RoundButton("✏️ Edit", RoundButton.ButtonStyle.OUTLINE);
        btnEdit.setPreferredSize(new Dimension(95, 32));
        btnEdit.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected >= 0 && selected < currentPetList.size()) {
                openPetDialog(currentPetList.get(selected));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a pet from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        RoundButton btnDelete = new RoundButton("🗑️ Delete", RoundButton.ButtonStyle.DANGER);
        btnDelete.setPreferredSize(new Dimension(105, 32));
        btnDelete.addActionListener(e -> performDelete());

        actionBtns.add(btnEdit);
        actionBtns.add(btnDelete);

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

    public void loadPets() {
        try {
            currentPetList = controller.getAllPets();
            populateTable(currentPetList);
        } catch (Exception e) {
            System.err.println("[PetPanel] Error loading pets: " + e.getMessage());
        }
    }

    private void performSearch() {
        String keyword = searchBar != null ? searchBar.getText() : "";
        String species = (String) cmbSpeciesFilter.getSelectedItem();
        try {
            currentPetList = controller.searchPets(keyword, species);
            populateTable(currentPetList);
        } catch (Exception e) {
            System.err.println("[PetPanel] Search error: " + e.getMessage());
        }
    }

    private void populateTable(List<Pet> list) {
        tableModel.setRowCount(0);
        for (Pet p : list) {
            tableModel.addRow(new Object[]{
                    p.getPetCode(),
                    p.getPetName(),
                    p.getSpecies(),
                    p.getBreed(),
                    p.getOwnerName() != null ? p.getOwnerName() : "Unknown",
                    "✏️ Edit  |  🗑️ Delete"
            });
        }
    }

    private void openPetDialog(Pet pet) {
        PetDialog dialog = new PetDialog(parentFrame, pet, this::loadPets);
        dialog.setVisible(true);
    }

    private void performDelete() {
        int selected = table.getSelectedRow();
        if (selected < 0 || selected >= currentPetList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a pet to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pet p = currentPetList.get(selected);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete pet patient '" + p.getPetName() + "' (" + p.getPetCode() + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = controller.deletePet(p.getPetId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Pet record deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadPets();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete pet: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
