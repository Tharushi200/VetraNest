package com.vetranest.controller;

import com.vetranest.dao.CustomerDAO;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Customer;
import com.vetranest.util.InputValidator;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller coordinating Customer business logic, input validation, and persistence.
 */
public class CustomerController {

    private final CustomerDAO customerDAO;

    public CustomerController() {
        this.customerDAO = new CustomerDAO();
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public List<Customer> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDAO.getAllCustomers();
        }
        return customerDAO.searchCustomers(keyword);
    }

    public boolean addCustomer(String name, String email, String phone, String address)
            throws ValidationException, SQLException {
        validateCustomerInput(name, email, phone);

        Customer c = new Customer();
        c.setName(name.trim());
        c.setEmail(email.trim());
        c.setPhone(phone.trim());
        c.setAddress(address != null ? address.trim() : "");

        return customerDAO.insertCustomer(c);
    }

    public boolean updateCustomer(int id, String name, String email, String phone, String address)
            throws ValidationException, SQLException {
        validateCustomerInput(name, email, phone);

        Customer c = new Customer();
        c.setCustomerId(id);
        c.setName(name.trim());
        c.setEmail(email.trim());
        c.setPhone(phone.trim());
        c.setAddress(address != null ? address.trim() : "");

        return customerDAO.updateCustomer(c);
    }

    public boolean deleteCustomer(int id) throws SQLException {
        return customerDAO.deleteCustomer(id);
    }

    public String getNextCustomerCode() {
        return customerDAO.getNextCustomerCode();
    }

    private void validateCustomerInput(String name, String email, String phone) throws ValidationException {
        InputValidator.validateNotEmpty(name, "Customer Name");
        InputValidator.validateEmail(email);
        InputValidator.validatePhone(phone);
    }
}
