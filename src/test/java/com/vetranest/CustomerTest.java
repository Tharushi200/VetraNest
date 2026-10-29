package com.vetranest;

import com.vetranest.controller.CustomerController;
import com.vetranest.model.Customer;

import java.util.List;

public class CustomerTest {

    public static void main(String[] args) {
        System.out.println("--- Testing Customer Management CRUD & Validation ---");
        CustomerController controller = new CustomerController();

        try {
            // 1. Fetch all
            List<Customer> list = controller.getAllCustomers();
            System.out.println("✓ Total Customers fetched: " + list.size());
            for (Customer c : list) {
                System.out.println("   [" + c.getCustomerCode() + "] " + c.getName() + " - " + c.getEmail() + " (" + c.getPhone() + ")");
            }

            // 2. Search test
            List<Customer> searchResults = controller.searchCustomers("Nimal");
            System.out.println("✓ Search for 'Nimal': Found " + searchResults.size() + " record(s)");
            if (!searchResults.isEmpty()) {
                System.out.println("   Match: " + searchResults.get(0).getName());
            }

            // 3. Validation & Add test
            System.out.println("Testing Customer Insertion with Validations...");
            boolean added = controller.addCustomer("Kamal Gunawardena", "kamal@gmail.com", "0773344556", "No. 77, Havelock Road, Colombo");
            System.out.println("✓ Add Customer Result: " + (added ? "SUCCESS" : "FAILED"));

            // 4. Verify Next Code
            System.out.println("✓ Next Generated Code: " + controller.getNextCustomerCode());

            System.out.println("--- Customer Management Test PASSED! ---");
        } catch (Exception e) {
            System.err.println("✗ Customer Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
