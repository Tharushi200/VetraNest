package com.vetranest.controller;

import com.vetranest.dao.CustomerDAO;
import com.vetranest.dao.PetDAO;
import com.vetranest.exception.ValidationException;
import com.vetranest.model.Customer;
import com.vetranest.model.Pet;
import com.vetranest.util.InputValidator;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller managing Pet registration, validation, and relational customer linking.
 */
public class PetController {

    private final PetDAO petDAO;
    private final CustomerDAO customerDAO;

    public PetController() {
        this.petDAO = new PetDAO();
        this.customerDAO = new CustomerDAO();
    }

    public List<Pet> getAllPets() throws SQLException {
        return petDAO.getAllPets();
    }

    public List<Pet> searchPets(String keyword, String species) throws SQLException {
        return petDAO.searchPets(keyword, species);
    }

    public List<Customer> getAllCustomersForSelection() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public boolean addPet(int customerId, String petName, String species, String breed,
                          String gender, Date dob, double weight, String color)
            throws ValidationException, SQLException {

        validatePetInput(customerId, petName, species);

        Pet p = new Pet();
        p.setCustomerId(customerId);
        p.setPetName(petName.trim());
        p.setSpecies(species != null ? species.trim() : "Dog");
        p.setBreed(breed != null && !breed.trim().isEmpty() ? breed.trim() : "Mixed");
        p.setGender(gender != null ? gender : "Male");
        p.setDob(dob);
        p.setWeight(weight);
        p.setColor(color != null ? color.trim() : "");

        return petDAO.insertPet(p);
    }

    public boolean updatePet(int petId, int customerId, String petName, String species, String breed,
                             String gender, Date dob, double weight, String color)
            throws ValidationException, SQLException {

        validatePetInput(customerId, petName, species);

        Pet p = new Pet();
        p.setPetId(petId);
        p.setCustomerId(customerId);
        p.setPetName(petName.trim());
        p.setSpecies(species != null ? species.trim() : "Dog");
        p.setBreed(breed != null && !breed.trim().isEmpty() ? breed.trim() : "Mixed");
        p.setGender(gender != null ? gender : "Male");
        p.setDob(dob);
        p.setWeight(weight);
        p.setColor(color != null ? color.trim() : "");

        return petDAO.updatePet(p);
    }

    public boolean deletePet(int petId) throws SQLException {
        return petDAO.deletePet(petId);
    }

    public String getNextPetCode() {
        return petDAO.getNextPetCode();
    }

    private void validatePetInput(int customerId, String petName, String species) throws ValidationException {
        if (customerId <= 0) {
            throw new ValidationException("Owner", "Please select a valid customer (pet owner).");
        }
        InputValidator.validateNotEmpty(petName, "Pet Name");
        InputValidator.validateNotEmpty(species, "Species");
    }
}
