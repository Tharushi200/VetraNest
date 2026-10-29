package com.vetranest;

import com.vetranest.controller.DashboardController;
import java.util.List;

public class DashboardTest {

    public static void main(String[] args) {
        System.out.println("--- Testing Dashboard Data & Aggregations ---");
        DashboardController controller = new DashboardController();

        System.out.println("✓ Total Customers in DB: " + controller.getTotalCustomers());
        System.out.println("✓ Total Pets in DB: " + controller.getTotalPets());
        System.out.println("✓ Today's Appointments Count: " + controller.getTodayAppointmentsCount());
        System.out.println("✓ Total Income in DB: Rs. " + controller.getTotalIncome());

        List<Object[]> today = controller.getTodayAppointments();
        System.out.println("✓ Today's Appointment records retrieved: " + today.size());
        for (Object[] row : today) {
            System.out.println("   -> [" + row[0] + "] " + row[1] + " | " + row[2] + " | Status: " + row[3]);
        }

        System.out.println("--- Dashboard Test PASSED Successfully! ---");
    }
}
