package com.gdldv.rental.entity;

public enum RentalStatus {
    PENDING,           // En attente
    CHECKED_OUT,       // Véhicule récupéré
    ACTIVE,            // Location en cours
    CHECKED_IN,        // Véhicule retourné
    COMPLETED,         // Location terminée
    CANCELLED          // Annulée
}
