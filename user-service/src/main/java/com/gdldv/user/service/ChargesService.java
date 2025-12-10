package com.gdldv.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargesService {

    private static final double PRICE_PER_KM = 100;      // 100 FCFA par km
    private static final double PRICE_PER_LITER = 700;   // 700 FCFA par litre
    private static final double PRICE_PER_HOUR_LATE = 5000; // 5000 FCFA par heure

    public double calculateCharges(
        Long initialMileage,
        Long finalMileage,
        String initialFuel,
        String finalFuel) {

        log.info("Calculating additional charges");

        double totalCharges = 0;

        // Calculate km charges
        long kmDriven = finalMileage - initialMileage;
        // Assuming first 200km included, rest charged
        long chargeableKm = Math.max(0, kmDriven - 200);
        double kmCharges = chargeableKm * PRICE_PER_KM;
        totalCharges += kmCharges;

        // Calculate fuel charges
        double fuelCharges = calculateFuelCharges(initialFuel, finalFuel);
        totalCharges += fuelCharges;

        log.info("Charges calculated: km={}, fuel={}, total={}",
            kmCharges, fuelCharges, totalCharges);

        return totalCharges;
    }

    private double calculateFuelCharges(String initialFuel, String finalFuel) {
        // Convert fuel levels to liters
        double initialLiters = fuelLevelToLiters(initialFuel);
        double finalLiters = fuelLevelToLiters(finalFuel);

        double litersMissing = initialLiters - finalLiters;

        if (litersMissing > 0) {
            return litersMissing * PRICE_PER_LITER;
        }

        return 0;
    }

    private double fuelLevelToLiters(String level) {
        switch (level) {
            case "Full": return 60;
            case "3/4": return 45;
            case "1/2": return 30;
            case "1/4": return 15;
            case "Empty": return 0;
            default: return 30;
        }
    }

    public double calculateLateCharges(long minutesLate) {
        long hoursLate = minutesLate / 60;
        return hoursLate * PRICE_PER_HOUR_LATE;
    }

    public double calculateDamageCharges(String damageDescription) {
        // Calculate based on damage severity
        // Minor: 10,000 FCFA, Moderate: 25,000 FCFA, Severe: 50,000+ FCFA
        if (damageDescription.toLowerCase().contains("severe") ||
            damageDescription.toLowerCase().contains("critical")) {
            return 50000;
        } else if (damageDescription.toLowerCase().contains("moderate")) {
            return 25000;
        } else {
            return 10000;
        }
    }
}
