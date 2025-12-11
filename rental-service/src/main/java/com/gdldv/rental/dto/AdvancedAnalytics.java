package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * GDLDV-542: DTO pour les analytics avancés
 * Inclut les prédictions, tendances et recommandations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedAnalytics {

    private LocalDateTime generatedAt;
    private String analysisType; // FORECAST, TREND, PATTERN, RECOMMENDATION

    // Prédictions de revenus
    private RevenueForecast revenueForecast;

    // Analyse de tendances
    private TrendAnalysis trendAnalysis;

    // Patterns détectés
    private List<Pattern> detectedPatterns;

    // Recommandations
    private List<Recommendation> recommendations;

    // Performance des véhicules
    private VehiclePerformance vehiclePerformance;

    // Segmentation clients
    private CustomerSegmentation customerSegmentation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueForecast {
        private Double nextMonthForecast;
        private Double nextQuarterForecast;
        private Double nextYearForecast;
        private Double confidenceLevel; // Niveau de confiance de la prédiction (0-100%)
        private String forecastMethod; // LINEAR, MOVING_AVERAGE, EXPONENTIAL
        private Map<String, Double> monthlyBreakdown; // Prédictions mois par mois
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendAnalysis {
        private String overallTrend; // UPWARD, DOWNWARD, STABLE, VOLATILE
        private Double growthRate; // Taux de croissance moyen (%)
        private Double volatility; // Volatilité des revenus (écart-type)
        private String seasonality; // STRONG, MODERATE, WEAK, NONE
        private List<String> peakMonths; // Mois de forte activité
        private List<String> lowMonths; // Mois de faible activité
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pattern {
        private String patternType; // SEASONAL, CYCLICAL, IRREGULAR
        private String description;
        private Double confidence; // Confiance dans la détection (0-100%)
        private String impact; // HIGH, MEDIUM, LOW
        private String recommendation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String category; // PRICING, MARKETING, FLEET, OPERATIONS
        private String title;
        private String description;
        private String priority; // HIGH, MEDIUM, LOW
        private Double potentialImpact; // Impact estimé sur le revenu (%)
        private List<String> actionItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehiclePerformance {
        private List<VehicleMetrics> topPerformers; // Top 5 véhicules les plus rentables
        private List<VehicleMetrics> underPerformers; // Véhicules sous-performants
        private Double averageUtilization; // Taux d'utilisation moyen
        private String recommendation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleMetrics {
        private Long vehicleId;
        private String brand;
        private String model;
        private Double revenue;
        private Double utilizationRate;
        private Integer bookingCount;
        private Double averageRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSegmentation {
        private Integer vipCustomers; // Clients avec >5 locations
        private Integer regularCustomers; // Clients avec 2-5 locations
        private Integer newCustomers; // Clients avec 1 location
        private Double vipRevenue;
        private Double regularRevenue;
        private Double newRevenue;
        private String recommendation;
    }
}
