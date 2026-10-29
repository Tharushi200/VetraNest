-- =============================================================================
-- VetraNest - Enterprise Pet Care & Veterinary Management System
-- Database Schema & Seed Data Script
-- Author: VetraNest Team
-- Compatible with MySQL 8.0+
-- =============================================================================

CREATE DATABASE IF NOT EXISTS vetranest_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vetranest_db;

-- -----------------------------------------------------------------------------
-- 1. Table: users
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('Admin', 'Veterinarian', 'Receptionist') DEFAULT 'Admin',
    security_question VARCHAR(255) DEFAULT 'What is the clinic license number?',
    security_answer VARCHAR(255) DEFAULT 'VN2026',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. Table: customers
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. Table: pets
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_code VARCHAR(20) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    pet_name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    gender ENUM('Male', 'Female') NOT NULL,
    dob DATE,
    weight DECIMAL(5,2) DEFAULT 0.00,
    color VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. Table: appointments (Transaction UI Table)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_code VARCHAR(20) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    pet_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    status ENUM('Pending', 'Confirmed', 'Completed', 'Cancelled') DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_pet FOREIGN KEY (pet_id) 
        REFERENCES pets(pet_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. Table: treatments (Clinical & Medication Records)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS treatments (
    treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NULL,
    pet_id INT NOT NULL,
    treatment_date DATE NOT NULL,
    diagnosis TEXT NOT NULL,
    medication TEXT NOT NULL,
    instructions TEXT,
    doctor_name VARCHAR(100) DEFAULT 'Dr. K. Perera',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_treatments_pet FOREIGN KEY (pet_id) 
        REFERENCES pets(pet_id) ON DELETE CASCADE,
    CONSTRAINT fk_treatments_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(appointment_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. Table: billings (Financial & Invoicing)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS billings (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_code VARCHAR(20) NOT NULL UNIQUE,
    appointment_id INT NULL,
    customer_id INT NOT NULL,
    service_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    medication_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status ENUM('Paid', 'Pending') DEFAULT 'Paid',
    payment_method ENUM('Cash', 'Card', 'Bank Transfer') DEFAULT 'Cash',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_billings_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_billings_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(appointment_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =============================================================================
-- SEED DATA (Matches Design Board Example & Demonstration Data)
-- =============================================================================

-- Seed Users: admin / admin123 (SHA-256 for admin123 is 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa82280f1a8230e88)
INSERT INTO users (username, password_hash, full_name, email, role, security_question, security_answer)
VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator', 'admin@vetranest.lk', 'Admin', 'What is the clinic license number?', 'VN2026'),
('dr_perera', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Dr. Kasun Perera', 'kasun.p@vetranest.lk', 'Veterinarian', 'What is the clinic license number?', 'VN2026')
ON DUPLICATE KEY UPDATE username=username;

-- Seed Customers
INSERT INTO customers (customer_code, name, email, phone, address)
VALUES
('C001', 'Nimal Perera', 'nimal@gmail.com', '0771234567', 'No. 45, Galle Road, Colombo 03'),
('C002', 'Sanduni Silva', 'sanduni@gmail.com', '0779876543', 'No. 12, Kandy Road, Kiribathgoda'),
('C003', 'Kasun Fernando', 'kasun@gmail.com', '0714567890', 'No. 88, High Level Road, Nugegoda'),
('C004', 'Dilini Wickramasinghe', 'dilini@gmail.com', '0766432109', 'No. 23, Station Road, Maharagama'),
('C005', 'Shashika Jayasinghe', 'shashika@gmail.com', '0788765432', 'No. 15, Lake Drive, Rajagiriya')
ON DUPLICATE KEY UPDATE customer_code=customer_code;

-- Seed Pets
INSERT INTO pets (pet_code, customer_id, pet_name, species, breed, gender, dob, weight, color)
VALUES
('P001', 1, 'Bella', 'Dog', 'Golden Retriever', 'Female', '2023-04-12', 26.50, 'Golden Cream'),
('P002', 2, 'Max', 'Cat', 'Persian', 'Male', '2024-01-15', 4.80, 'White & Gray'),
('P003', 3, 'Luna', 'Dog', 'Labrador', 'Female', '2022-11-20', 29.00, 'Chocolate Brown'),
('P004', 4, 'Charlie', 'Cat', 'Siamese', 'Male', '2023-08-05', 4.20, 'Fawn Cream'),
('P005', 5, 'Milo', 'Dog', 'Beagle', 'Male', '2024-03-10', 10.50, 'Tricolor')
ON DUPLICATE KEY UPDATE pet_code=pet_code;

-- Seed Appointments (Matches UI Mockup)
INSERT INTO appointments (appointment_code, customer_id, pet_id, appointment_date, time_slot, service_type, status, notes)
VALUES
('A001', 1, 1, CURDATE(), '09:00 AM', 'Vaccination', 'Pending', 'Rabies booster vaccine scheduled'),
('A002', 2, 2, CURDATE(), '10:30 AM', 'Checkup', 'Confirmed', 'Routine oral hygiene & health review'),
('A003', 3, 3, CURDATE(), '01:00 PM', 'Grooming', 'Confirmed', 'Full bath and nail trim'),
('A004', 4, 4, CURDATE(), '03:30 PM', 'Treatment', 'Pending', 'Follow-up inspection for skin irritation'),
('A005', 5, 5, CURDATE() + INTERVAL 1 DAY, '10:00 AM', 'Checkup', 'Confirmed', 'Puppy growth check')
ON DUPLICATE KEY UPDATE appointment_code=appointment_code;

-- Seed Treatments
INSERT INTO treatments (appointment_id, pet_id, treatment_date, diagnosis, medication, instructions, doctor_name)
VALUES
(4, 4, CURDATE(), 'Mild skin infection (Allergic dermatitis)', 'Antibiotics (5 days) + Antihistamine ointment', 'Apply twice daily after gentle cleansing. Follow up in 5 days.', 'Dr. K. Perera'),
(1, 1, CURDATE() - INTERVAL 14 DAY, 'Annual Rabies & DHPP Vaccination', 'Rabies Vaccine 1.0ml SC', 'Observation for 24 hours. Ensure hydration.', 'Dr. K. Perera'),
(2, 2, CURDATE() - INTERVAL 30 DAY, 'Mild Ear Mites Infestation', 'Acarexx Otic Drops 0.5ml', 'Administer into ear canal twice daily for 7 days.', 'Dr. K. Perera');

-- Seed Billings (Real Monthly Trend across Jun - Oct, Total = Rs. 48,500)
INSERT INTO billings (bill_code, appointment_id, customer_id, service_fee, medication_fee, discount, total_amount, payment_status, payment_method, payment_date)
VALUES
('B001', 1, 1, 3500.00, 2000.00, 500.00, 5000.00, 'Paid', 'Card', '2026-06-15 10:00:00'),
('B002', 2, 2, 2500.00, 1500.00, 0.00, 4000.00, 'Paid', 'Cash', '2026-07-20 11:30:00'),
('B003', 3, 3, 4500.00, 4000.00, 0.00, 8500.00, 'Paid', 'Card', '2026-08-14 14:15:00'),
('B004', 4, 4, 8000.00, 4000.00, 0.00, 12000.00, 'Paid', 'Cash', '2026-09-22 09:45:00'),
('B005', 5, 5, 12000.00, 7000.00, 0.00, 19000.00, 'Paid', 'Bank Transfer', '2026-10-27 16:00:00')
ON DUPLICATE KEY UPDATE total_amount=total_amount;
