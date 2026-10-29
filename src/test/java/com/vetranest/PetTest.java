package com.vetranest;

import com.vetranest.controller.PetController;
import com.vetranest.model.Pet;

import java.util.List;

public class PetTest {

    public static void main(String[] args) {
        System.out.println("--- Testing Pet Management CRUD & Relational Queries ---");
        PetController controller = new PetController();

        try {
            // 1. Fetch all pets (joined with owner names)
            List<Pet> all = controller.getAllPets();
            System.out.println("✓ Total Pets fetched: " + all.size());
            for (Pet p : all) {
                System.out.println("   [" + p.getPetCode() + "] " + p.getPetName()
                        + " (" + p.getSpecies() + " - " + p.getBreed() + ")"
                        + "  Owner: " + p.getOwnerName());
            }

            // 2. Species Filter: Dogs only
            List<Pet> dogs = controller.searchPets("", "Dog");
            System.out.println("✓ Filtered by 'Dog': " + dogs.size() + " result(s)");

            // 3. Keyword Search
            List<Pet> results = controller.searchPets("Bella", "");
            System.out.println("✓ Search 'Bella': " + results.size() + " result(s)");
            if (!results.isEmpty()) {
                System.out.println("   Match: " + results.get(0).getPetName() + " owned by " + results.get(0).getOwnerName());
            }

            // 4. Next Pet Code
            System.out.println("✓ Next Generated Code: " + controller.getNextPetCode());

            System.out.println("--- Pet Management Test PASSED! ---");
        } catch (Exception e) {
            System.err.println("✗ Pet Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
