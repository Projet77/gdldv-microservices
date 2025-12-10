package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.PricingBreakdown;
import com.gdldv.reservation.dto.PricingRequest;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.ReservationOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final VehicleClient vehicleClient;

    private static final Double TAX_RATE = 0.18; // 18% de taxes

    /**
     * Calcule le prix total d'une réservation avec détails
     */
    public PricingBreakdown calculatePricing(PricingRequest request) {
        log.info("Calculating pricing for vehicle {} from {} to {}",
                request.getVehicleId(), request.getStartDate(), request.getEndDate());

        // 1. Récupérer les informations du véhicule
        VehicleDTO vehicle = vehicleClient.getVehicleById(request.getVehicleId());
        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with id: " + request.getVehicleId());
        }

        // 2. Calculer le nombre de jours
        long numberOfDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (numberOfDays <= 0) {
            throw new RuntimeException("End date must be after start date");
        }

        // 3. Calculer le prix de base
        double basePrice = numberOfDays * vehicle.getDailyPrice();

        // 4. Calculer le prix des options
        double optionsPrice = 0.0;
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            optionsPrice = request.getOptions().stream()
                .mapToDouble(opt -> opt.getOptionPrice() * opt.getQuantity())
                .sum();
        }

        // 5. Calculer le sous-total
        double subtotal = basePrice + optionsPrice;

        // 6. Calculer les taxes (18%)
        double taxAmount = subtotal * TAX_RATE;

        // 7. Calculer le prix total TTC
        double totalPrice = subtotal + taxAmount;

        log.info("Pricing calculated: base={}, options={}, subtotal={}, tax={}, total={}",
                basePrice, optionsPrice, subtotal, taxAmount, totalPrice);

        // 8. Retourner le détail
        return PricingBreakdown.builder()
            .vehicleId(vehicle.getId())
            .vehicleBrand(vehicle.getBrand())
            .vehicleModel(vehicle.getModel())
            .dailyPrice(vehicle.getDailyPrice())
            .numberOfDays(numberOfDays)
            .basePrice(basePrice)
            .optionsPrice(optionsPrice)
            .subtotal(subtotal)
            .taxRate(TAX_RATE)
            .taxAmount(taxAmount)
            .totalPrice(totalPrice)
            .build();
    }
}
