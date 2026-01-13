-- Script pour mettre à jour le statut des réservations existantes
USE gdldv_reservation_db;

-- Mettre à jour toutes les réservations PENDING en CONFIRMED
UPDATE reservations
SET status = 'CONFIRMED'
WHERE status = 'PENDING';

-- Vérifier le résultat
SELECT id, confirmation_number, status, start_date, end_date, total_price
FROM reservations
ORDER BY id;
