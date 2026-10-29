package com.vetranest.controller;

import com.vetranest.dao.DashboardDAO;

import java.util.List;
import java.util.Map;

/**
 * Controller providing aggregated data for the main dashboard view.
 */
public class DashboardController {

    private final DashboardDAO dashboardDAO;

    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
    }

    public int getTotalCustomers() {
        return dashboardDAO.getTotalCustomers();
    }

    public int getTotalPets() {
        return dashboardDAO.getTotalPets();
    }

    public int getTodayAppointmentsCount() {
        return dashboardDAO.getTodayAppointmentsCount();
    }

    public double getTotalIncome() {
        return dashboardDAO.getTotalIncome();
    }

    public List<Object[]> getTodayAppointments() {
        return dashboardDAO.getTodayAppointments();
    }

    public Map<String, Double> getMonthlyRevenue() {
        return dashboardDAO.getMonthlyRevenue();
    }
}
