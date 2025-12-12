package com.gdldv.reservation.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingBreakdown {

    private Long vehicleId;
    private String vehicleBrand;
    private String vehicleModel;
    private Double dailyPrice;
    private Long numberOfDays;
    private Double basePrice;           // Prix de base (dailyPrice × numberOfDays)
    private Double optionsPrice;        // Prix total des options
    private Double subtotal;            // basePrice + optionsPrice
    private Double taxRate;             // Taux de taxe (ex: 0.18 pour 18%)
    private Double taxAmount;           // Montant des taxes
    private Double totalPrice;          // Prix total TTC
}
