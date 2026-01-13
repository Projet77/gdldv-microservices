USE gdldv_vehicle_db;

-- DÉSACTIVER LES CONTRÔLES DE CLÉ ÉTRANGÈRE POUR VIDER LES TABLES PROPREMENT
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE vehicle_images;
TRUNCATE TABLE vehicles;
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- GAMME LUXE & SUPERCAR
-- ==========================================

-- 1. Lamborghini Urus
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (1, 'Lamborghini', 'Urus', 2024, 'SUV Sport', 250000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'Le SUV le plus rapide du monde. Puissance brute et luxe absolu.', 'DK-1001-AA', 5000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (1, '/images/suv_car.png');

-- 2. Ferrari F8 Tributo
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (2, 'Ferrari', 'F8 Tributo', 2023, 'Supercar', 350000, 2, 'AUTOMATIQUE', 'ESSENCE', 0, 'Une icône de Maranello. V8 biturbo, 720 chevaux.', 'DK-2002-BB', 3000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (2, '/images/sport_car.png');

-- 3. Mercedes-Benz G63 AMG
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (3, 'Mercedes-Benz', 'G63 AMG', 2024, 'SUV Luxe', 200000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'Le roi des 4x4. Imposant, bruyant, luxueux.', 'DK-3003-CC', 8000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (3, '/images/suv_car.png');

-- 4. Porsche 911 GT3
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (4, 'Porsche', '911 GT3', 2023, 'Sport', 280000, 2, 'AUTOMATIQUE', 'ESSENCE', 0, 'La perfection sur circuit et sur route.', 'DK-4004-DD', 4500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (4, '/images/sport_car.png');

-- 5. Range Rover Autobiography
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (5, 'Range Rover', 'Autobiography', 2024, 'SUV Luxe', 180000, 5, 'AUTOMATIQUE', 'DIESEL', 1, 'Le summum du confort britannique.', 'DK-5005-EE', 12000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (5, '/images/suv_car.png');

-- 6. Audi RSQ8
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (6, 'Audi', 'RSQ8', 2023, 'SUV Sport', 190000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'La cousine technique de l''Urus, plus discrète mais tout aussi redoutable.', 'DK-6006-FF', 9500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (6, '/images/suv_car.png');

-- 7. Bentley Continental GT
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (7, 'Bentley', 'Continental GT', 2022, 'Grand Tourisme', 300000, 4, 'AUTOMATIQUE', 'ESSENCE', 1, 'Voyagez en première classe, au volant.', 'DK-7007-GG', 6000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (7, '/images/hero.png');

-- 8. Rolls Royce Cullinan
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (8, 'Rolls Royce', 'Cullinan', 2024, 'Ultra Luxe', 500000, 4, 'AUTOMATIQUE', 'ESSENCE', 1, 'Le SUV le plus luxueux jamais construit.', 'DK-8008-HH', 2000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (8, '/images/suv_car.png');

-- 9. McLaren 720S
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (9, 'McLaren', '720S', 2023, 'Supercar', 320000, 2, 'AUTOMATIQUE', 'ESSENCE', 0, 'Une aérodynamique sculptée par le vent.', 'DK-9009-II', 3500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (9, '/images/sport_car.png');

-- 10. Tesla Model X Plaid
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (10, 'Tesla', 'Model X Plaid', 2024, 'Électrique', 150000, 6, 'AUTOMATIQUE', 'ELECTRIQUE', 1, '1020 chevaux, électrique, familial. Falcon Wings.', 'DK-1010-JJ', 1500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (10, '/images/suv_car.png');

-- 11. BMW M4 Competition
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (11, 'BMW', 'M4 Competition', 2024, 'Sport', 160000, 4, 'AUTOMATIQUE', 'ESSENCE', 1, 'Le coupé sportif par excellence.', 'DK-1111-KK', 5500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (11, '/images/sport_car.png');

-- 12. Aston Martin DBX 707
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (12, 'Aston Martin', 'DBX 707', 2023, 'SUV Sport', 260000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'Le SUV de James Bond. 707 chevaux.', 'DK-1212-LL', 7000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (12, '/images/suv_car.png');

-- 13. Porsche Taycan Turbo S
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (13, 'Porsche', 'Taycan Turbo S', 2024, 'Électrique', 220000, 4, 'AUTOMATIQUE', 'ELECTRIQUE', 1, 'L''âme de Porsche, le cœur électrique.', 'DK-1313-MM', 4000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (13, '/images/sport_car.png');

-- 14. Maserati MC20
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (14, 'Maserati', 'MC20', 2023, 'Supercar', 290000, 2, 'AUTOMATIQUE', 'ESSENCE', 0, 'Le trident de retour au sommet.', 'DK-1414-NN', 2500, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (14, '/images/sport_car.png');

-- 15. Cadillac Escalade V
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (15, 'Cadillac', 'Escalade V', 2024, 'SUV XXL', 210000, 7, 'AUTOMATIQUE', 'ESSENCE', 1, 'L''Amérique en mode XXL et supercharged.', 'DK-1515-OO', 3000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (15, '/images/suv_car.png');

-- 16. Bugatti Chiron
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (16, 'Bugatti', 'Chiron', 2022, 'Hypercar', 1500000, 2, 'AUTOMATIQUE', 'ESSENCE', 0, 'L''ultime machine de vitesse.', 'DK-1616-PP', 1000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (16, '/images/sport_car.png');

-- ==========================================
-- GAMME ACCESSIBLE & CITADINES
-- ==========================================

-- 17. Peugeot 208
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (17, 'Peugeot', '208', 2023, 'Citadine', 30000, 5, 'MANUELLE', 'ESSENCE', 1, 'La citadine préférée des français. Compacte et stylée.', 'DK-1717-QQ', 15000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (17, '/images/city_car.png');

-- 18. Renault Clio V
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (18, 'Renault', 'Clio V', 2023, 'Citadine', 25000, 5, 'MANUELLE', 'DIESEL', 1, 'Polyvalente et confortable, idéale pour la ville.', 'DK-1818-RR', 18000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (18, '/images/city_car.png');

-- 19. Toyota Corolla
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (19, 'Toyota', 'Corolla', 2022, 'Berline', 40000, 5, 'AUTOMATIQUE', 'HYBRIDE', 1, 'La fiabilité légendaire avec une consommation minimale.', 'DK-1919-SS', 22000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (19, '/images/sedan_car.png');

-- 20. Dacia Duster
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (20, 'Dacia', 'Duster', 2023, 'SUV Éco', 35000, 5, 'MANUELLE', 'DIESEL', 1, 'Le SUV robuste et accessible pour l''aventure.', 'DK-2020-TT', 19000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (20, '/images/compact_suv.png');

-- 21. Hyundai Tucson
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (21, 'Hyundai', 'Tucson', 2023, 'SUV Compact', 50000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'Design futuriste et confort premium pour la famille.', 'DK-2121-UU', 14000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (21, '/images/compact_suv.png');

-- 22. Kia Picanto
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (22, 'Kia', 'Picanto', 2023, 'Citadine', 20000, 4, 'MANUELLE', 'ESSENCE', 0, 'La petite citadine nerveuse pour se faufiler partout.', 'DK-2222-VV', 5000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (22, '/images/city_car.png');

-- 23. Citroën C3
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (23, 'Citroën', 'C3', 2023, 'Citadine', 28000, 5, 'MANUELLE', 'ESSENCE', 1, 'Le confort Citroën dans un format compact.', 'DK-2323-WW', 9000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (23, '/images/city_car.png');

-- 24. Volkswagen Polo
INSERT INTO vehicles (id, brand, model, year, category, daily_price, seats, transmission, fuel_type, baby_seat, description, license_plate, mileage, status, is_active, created_at, updated_at) 
VALUES (24, 'Volkswagen', 'Polo', 2022, 'Citadine', 32000, 5, 'AUTOMATIQUE', 'ESSENCE', 1, 'La qualité allemande en format poche.', 'DK-2424-XX', 16000, 'AVAILABLE', 1, NOW(), NOW());
INSERT INTO vehicle_images (vehicle_id, image_url) VALUES (24, '/images/city_car.png');
