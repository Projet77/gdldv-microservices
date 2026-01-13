-- Script pour remettre les réservations en PENDING
USE gdldv_reservation_db;

-- Mettre toutes les réservations en PENDING
UPDATE reservations
SET status = 'PENDING'
WHERE status IN ('CONFIRMED', 'ACTIVE');

-- Vérifier le résultat
SELECT id, confirmation_number, status, start_date, end_date, total_price
FROM reservations
ORDER BY id;
