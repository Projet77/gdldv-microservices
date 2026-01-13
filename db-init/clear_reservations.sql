-- Script pour nettoyer les réservations de test
USE gdldv_reservation_db;

-- Supprimer toutes les réservations
DELETE FROM reservation_options;
DELETE FROM reservations;

-- Réinitialiser l'auto-increment
ALTER TABLE reservations AUTO_INCREMENT = 1;

-- Vérifier que c'est vide
SELECT COUNT(*) as total_reservations FROM reservations;
