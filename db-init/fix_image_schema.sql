USE gdldv_vehicle_db;

-- Fix "Data too long" error by changing image_url from VARCHAR(255) to LONGTEXT
ALTER TABLE vehicle_images MODIFY image_url LONGTEXT;

-- Ensure description is also large enough (optional but good practice)
ALTER TABLE vehicles MODIFY description TEXT;
