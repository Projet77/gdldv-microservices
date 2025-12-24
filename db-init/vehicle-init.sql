-- =============================================
-- GDLDV Vehicle Database Initialization
-- =============================================

-- Set character set
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- The database is already created by Docker environment variables
-- USE gdldv_vehicle_db;

-- Tables will be auto-created by JPA/Hibernate with ddl-auto=update

-- Optional: Insert sample data for testing
-- Uncomment below if you want to insert test data

/*
INSERT INTO vehicle (id, brand, model, year, category, daily_rate, status, vin, license_plate, color, mileage, fuel_type, transmission, seats, features, created_at, updated_at)
VALUES
(1, 'Toyota', 'Camry', 2023, 'SEDAN', 50.00, 'AVAILABLE', 'VIN123456789', 'ABC-1234', 'Silver', 5000, 'GASOLINE', 'AUTOMATIC', 5, 'GPS,Bluetooth,Backup Camera', NOW(), NOW()),
(2, 'Honda', 'CR-V', 2023, 'SUV', 70.00, 'AVAILABLE', 'VIN987654321', 'XYZ-5678', 'Black', 3000, 'GASOLINE', 'AUTOMATIC', 7, 'GPS,Sunroof,Leather Seats', NOW(), NOW()),
(3, 'Tesla', 'Model 3', 2024, 'SEDAN', 90.00, 'AVAILABLE', 'VIN456789123', 'TES-9999', 'White', 1000, 'ELECTRIC', 'AUTOMATIC', 5, 'Autopilot,GPS,Premium Sound', NOW(), NOW());
*/

SET FOREIGN_KEY_CHECKS = 1;
