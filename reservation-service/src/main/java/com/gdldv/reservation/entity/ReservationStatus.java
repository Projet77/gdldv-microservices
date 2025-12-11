package com.gdldv.reservation.entity;

public enum ReservationStatus {
    PENDING,      // En attente de paiement
    CONFIRMED,    // Paiement reçu
    ACTIVE,       // Location en cours
    COMPLETED,    // Location terminée
    CANCELLED     // Annulée
}
