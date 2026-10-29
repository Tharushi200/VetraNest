package com.vetranest.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Pet Entity Model representing clinical pet patients.
 */
public class Pet {

    private int petId;
    private String petCode;
    private int customerId;
    private String ownerName; // Joined from customers table
    private String petName;
    private String species;   // Dog, Cat, Bird, Rabbit, etc.
    private String breed;
    private String gender;    // Male, Female
    private Date dob;
    private double weight;
    private String color;
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public Pet() {
    }

    /**
     * Parameterized constructor for registering a clinical pet patient.
     *
     * @param petCode    Unique pet identifier code (e.g. P001)
     * @param customerId Foreign key ID of pet's registered owner
     * @param petName    Name of the pet
     * @param species    Species classification (e.g. Dog, Cat, Bird)
     * @param breed      Specific breed
     * @param gender     Gender (Male, Female)
     */
    public Pet(String petCode, int customerId, String petName, String species, String breed, String gender) {
        this.petCode = petCode;
        this.customerId = customerId;
        this.petName = petName;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetCode() {
        return petCode;
    }

    public void setPetCode(String petCode) {
        this.petCode = petCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return petName + " (" + species + " - " + petCode + ")";
    }
}
