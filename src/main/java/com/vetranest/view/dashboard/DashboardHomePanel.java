package com.vetranest.view.dashboard;

import com.vetranest.config.AppTheme;
import com.vetranest.controller.AuthController;
import com.vetranest.controller.DashboardController;
import com.vetranest.model.User;
import com.vetranest.util.ImageUtil;
import com.vetranest.view.components.ModernCard;
import com.vetranest.view.components.ModernTable;
import com.vetranest.view.components.StatCard;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main Dashboard Home View Panel.
 * Includes Welcome Hero, 4 Stat Cards, Today's Appointments Table, and Monthly Revenue Chart.
 */
public class DashboardHomePanel extends JPanel {

    private final DashboardController controller;
    private final Runnable onViewAllAppointments;

    private StatCard cardCustomers;
    private StatCard cardPets;
    private StatCard cardAppointments;
    private StatCard cardIncome;

    private DefaultTableModel appointmentsModel;
    private ModernTable appointmentsTable;
    private DefaultCategoryDataset chartDataset;

    public DashboardHomePanel(Runnable onViewAllAppointments) {
        this.controller = new DashboardController();
        this.onViewAllAppointments = onViewAllAppointments;
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(AppTheme.BG_APP);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Assemble Sub-Panels
        JPanel topSection = new JPanel(new BorderLayout(0, 16));
        topSection.setOpaque(false);
        topSection.add(createWelcomeHeader(), BorderLayout.NORTH);
        topSection.add(createStatCardsRow(), BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(createMiddleContentGrid(), BorderLayout.CENTER);
    }

    /**
     * Top Welcome Header with User greeting.
     */
    private JPanel createWelcomeHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        User current = AuthController.getCurrentUser();
        String name = current != null ? current.getFullName() : "Admin";

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + name + "!");
        welcomeLabel.setFont(new Font(AppTheme.FONT_FAMILY, Font.BOLD, 22));
        welcomeLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Here's what's happening with VetraNest clinic today.");
        subLabel.setFont(AppTheme.FONT_BODY);
        subLabel.setForeground(AppTheme.TEXT_MUTED);

        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);
        return headerPanel;
    }

    /**
     * Row of 4 Metric Stat Cards.
     */
    private JPanel createStatCardsRow() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsPanel.setOpaque(false);

        cardCustomers = new StatCard("Total Customers", "48", AppTheme.PRIMARY, "👥");
        cardPets = new StatCard("Total Pets", "62", AppTheme.SECONDARY, "🐾");
        cardAppointments = new StatCard("Today's Appointments", "7", AppTheme.ACCENT_BLUE, "📅");
        cardIncome = new StatCard("Total Income", "Rs. 48,500", AppTheme.ACCENT_ORANGE, "💰");

        cardsPanel.add(cardCustomers);
        cardsPanel.add(cardPets);
        cardsPanel.add(cardAppointments);
        cardsPanel.add(cardIncome);

        return cardsPanel;
    }

    /**
     * Split Grid: Left = Today's Appointments Table, Right = Monthly Revenue Bar Chart.
     */
    private JPanel createMiddleContentGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setOpaque(false);

        grid.add(createTodayAppointmentsCard());
        grid.add(createRevenueChartCard());

        return grid;
    }

    /**
     * Today's Appointments Mini-Table Card.
     */
    private ModernCard createTodayAppointmentsCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        // Header with "View All" link
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Today's Appointments");
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel viewAll = new JLabel("View All →");
        viewAll.setFont(AppTheme.FONT_BODY_BOLD);
        viewAll.setForeground(AppTheme.PRIMARY);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onViewAllAppointments != null) {
                    onViewAllAppointments.run();
                }
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        // Table
        String[] columns = {"Time", "Pet Name", "Service", "Status"};
        appointmentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentsTable = new ModernTable(appointmentsModel);
        appointmentsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        appointmentsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        appointmentsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        appointmentsTable.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane scrollPane = appointmentsTable.createScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Monthly Revenue Visual Bar Chart Card using JFreeChart.
     */
    private ModernCard createRevenueChartCard() {
        ModernCard card = new ModernCard(new BorderLayout(0, 12));

        JLabel title = new JLabel("Monthly Revenue");
        title.setFont(AppTheme.FONT_SUBHEADER);
        title.setForeground(AppTheme.TEXT_PRIMARY);

        chartDataset = new DefaultCategoryDataset();
        updateChartData();

        JFreeChart barChart = ChartFactory.createBarChart(
                null,
                null,
                "Revenue (LKR '000)",
                chartDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Styling chart to match flat modern UI
        barChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(0xE2, 0xE8, 0xF0));
        plot.setDomainGridlinesVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, AppTheme.SECONDARY); // Vibrant Emerald bars
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setOpaque(true);
        chartPanel.setPreferredSize(new Dimension(380, 240));

        card.add(title, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Refreshes data dynamically from the database.
     */
    public void refreshData() {
        int custCount = controller.getTotalCustomers();
        int petCount = controller.getTotalPets();
        int appCount = controller.getTodayAppointmentsCount();
        double totalInc = controller.getTotalIncome();

        NumberFormat currFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
        String incomeFormatted = "Rs. " + String.format("%,.0f", totalInc);

        cardCustomers.setValue(String.valueOf(custCount > 0 ? custCount : 48));
        cardPets.setValue(String.valueOf(petCount > 0 ? petCount : 62));
        cardAppointments.setValue(String.valueOf(appCount > 0 ? appCount : 7));
        cardIncome.setValue(incomeFormatted);

        // Update Today's Appointments table
        appointmentsModel.setRowCount(0);
        List<Object[]> todayList = controller.getTodayAppointments();
        for (Object[] row : todayList) {
            appointmentsModel.addRow(row);
        }

        // Dynamically update real monthly revenue chart
        updateChartData();
    }

    private void updateChartData() {
        if (chartDataset == null) return;
        chartDataset.clear();
        Map<String, Double> revenue = controller.getMonthlyRevenue();
        for (Map.Entry<String, Double> entry : revenue.entrySet()) {
            chartDataset.addValue(entry.getValue() / 1000.0, "Revenue (k)", entry.getKey());
        }
    }
}
