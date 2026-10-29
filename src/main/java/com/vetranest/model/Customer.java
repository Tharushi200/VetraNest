package com.vetranest.model;

import java.sql.Timestamp;

/**
 * Customer Entity Model representing client pet owners.
 */
public class Customer {

    private int customerId;
    private String customerCode;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Timestamp createdAt;

    public Customer() {
    }

    public Customer(String customerCode, String name, String email, String phone, String address) {
        this.customerCode = customerCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name + " (" + customerCode + ")";
    }
}
