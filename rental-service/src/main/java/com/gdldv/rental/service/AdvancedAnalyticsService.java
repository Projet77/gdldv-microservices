package com.gdldv.rental.service;

import com.gdldv.rental.dto.AdvancedAnalytics;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.RentalStatus;
import com.gdldv.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GDLDV-542: Service d'analytics avancés
 * Génère des prédictions, tendances et recommandations basées sur les données historiques
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedAnalyticsService {

    private final RentalRepository rentalRepository;

    /**
     * Générer une analyse complète avec prédictions et recommandations
     */
    @Transactional(readOnly = true)
    public AdvancedAnalytics generateAdvancedAnalytics() {
        log.info("Génération des analytics avancés");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);

        // Récupérer les données historiques (1 an)
        List<Rental> historicalData = rentalRepository.findByCreatedAtBetween(oneYearAgo, now);

        // Générer les différentes analyses
        AdvancedAnalytics.RevenueForecast revenueForecast = generateRevenueForecast(historicalData);
        AdvancedAnalytics.TrendAnalysis trendAnalysis = analyzeTrends(historicalData);
        List<AdvancedAnalytics.Pattern> patterns = detectPatterns(historicalData);
        List<AdvancedAnalytics.Recommendation> recommendations = generateRecommendations(
                historicalData, trendAnalysis, revenueForecast);
        AdvancedAnalytics.VehiclePerformance vehiclePerformance = analyzeVehiclePerformance(historicalData);
        AdvancedAnalytics.CustomerSegmentation customerSegmentation = segmentCustomers(historicalData);

        return AdvancedAnalytics.builder()
                .generatedAt(now)
                .analysisType("COMPREHENSIVE")
                .revenueForecast(revenueForecast)
                .trendAnalysis(trendAnalysis)
                .detectedPatterns(patterns)
                .recommendations(recommendations)
                .vehiclePerformance(vehiclePerformance)
                .customerSegmentation(customerSegmentation)
                .build();
    }

    /**
     * Générer des prédictions de revenus
     * Utilise une moyenne mobile pondérée pour les prédictions
     */
    private AdvancedAnalytics.RevenueForecast generateRevenueForecast(List<Rental> historicalData) {
        log.info("Génération des prédictions de revenus");

        // Calculer les revenus mensuels des 12 derniers mois
        Map<YearMonth, Double> monthlyRevenue = historicalData.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .filter(r -> r.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getCreatedAt()),
                        Collectors.summingDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice().doubleValue() : 0.0)
                ));

        // Calculer les prédictions avec moyenne mobile pondérée
        List<Double> revenueValues = monthlyRevenue.values().stream()
                .sorted()
                .collect(Collectors.toList());

        double nextMonthForecast = calculateWeightedMovingAverage(revenueValues, 3);
        double nextQuarterForecast = nextMonthForecast * 3 * 1.05; // Légère croissance attendue
        double nextYearForecast = nextMonthForecast * 12 * 1.10; // Croissance annuelle

        // Calculer le niveau de confiance (basé sur la variance)
        double variance = calculateVariance(revenueValues);
        double confidenceLevel = Math.max(60, Math.min(95, 100 - (variance / 1000)));

        // Prédictions mensuelles pour les 6 prochains mois
        Map<String, Double> monthlyBreakdown = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth currentMonth = YearMonth.now();

        for (int i = 1; i <= 6; i++) {
            YearMonth futureMonth = currentMonth.plusMonths(i);
            double forecast = nextMonthForecast * (1 + (i * 0.02)); // Croissance progressive
            monthlyBreakdown.put(futureMonth.format(formatter), Math.round(forecast * 100.0) / 100.0);
        }

        return AdvancedAnalytics.RevenueForecast.builder()
                .nextMonthForecast(Math.round(nextMonthForecast * 100.0) / 100.0)
                .nextQuarterForecast(Math.round(nextQuarterForecast * 100.0) / 100.0)
                .nextYearForecast(Math.round(nextYearForecast * 100.0) / 100.0)
                .confidenceLevel(Math.round(confidenceLevel * 100.0) / 100.0)
                .forecastMethod("WEIGHTED_MOVING_AVERAGE")
                .monthlyBreakdown(monthlyBreakdown)
                .build();
    }

    /**
     * Analyser les tendances
     */
    private AdvancedAnalytics.TrendAnalysis analyzeTrends(List<Rental> historicalData) {
        log.info("Analyse des tendances");

        // Grouper par mois
        Map<YearMonth, Double> monthlyRevenue = historicalData.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .filter(r -> r.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getCreatedAt()),
                        Collectors.summingDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice().doubleValue() : 0.0)
                ));

        List<Map.Entry<YearMonth, Double>> sortedMonths = monthlyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        // Calculer le taux de croissance moyen
        double growthRate = calculateGrowthRate(sortedMonths);

        // Déterminer la tendance globale
        String overallTrend = determineOverallTrend(growthRate);

        // Calculer la volatilité
        List<Double> revenueValues = sortedMonths.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        double volatility = calculateStandardDeviation(revenueValues);

        // Analyser la saisonnalité
        String seasonality = analyzeSeasonality(monthlyRevenue);

        // Identifier les mois de pic et creux
        List<String> peakMonths = identifyPeakMonths(monthlyRevenue);
        List<String> lowMonths = identifyLowMonths(monthlyRevenue);

        return AdvancedAnalytics.TrendAnalysis.builder()
                .overallTrend(overallTrend)
                .growthRate(Math.round(growthRate * 100.0) / 100.0)
                .volatility(Math.round(volatility * 100.0) / 100.0)
                .seasonality(seasonality)
                .peakMonths(peakMonths)
                .lowMonths(lowMonths)
                .build();
    }

    /**
     * Détecter les patterns dans les données
     */
    private List<AdvancedAnalytics.Pattern> detectPatterns(List<Rental> historicalData) {
        log.info("Détection des patterns");
        List<AdvancedAnalytics.Pattern> patterns = new ArrayList<>();

        // Pattern 1: Saisonnalité
        Map<Integer, Long> rentalsByMonth = historicalData.stream()
                .filter(r -> r.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().getMonthValue(),
                        Collectors.counting()
                ));

        long maxRentals = rentalsByMonth.values().stream().max(Long::compare).orElse(0L);
        long minRentals = rentalsByMonth.values().stream().min(Long::compare).orElse(0L);

        if (maxRentals > minRentals * 1.5) {
            patterns.add(AdvancedAnalytics.Pattern.builder()
                    .patternType("SEASONAL")
                    .description("Variations saisonnières détectées avec pics en été")
                    .confidence(85.0)
                    .impact("HIGH")
                    .recommendation("Ajuster les prix et la disponibilité de la flotte selon les saisons")
                    .build());
        }

        // Pattern 2: Tendance week-end
        Map<Boolean, Long> rentalsByWeekend = historicalData.stream()
                .filter(r -> r.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().getDayOfWeek().getValue() >= 6,
                        Collectors.counting()
                ));

        long weekendRentals = rentalsByWeekend.getOrDefault(true, 0L);
        long weekdayRentals = rentalsByWeekend.getOrDefault(false, 0L);

        if (weekendRentals > weekdayRentals * 0.5) { // Plus de 50% du volume weekday
            patterns.add(AdvancedAnalytics.Pattern.builder()
                    .patternType("WEEKLY")
                    .description("Forte demande durant les week-ends")
                    .confidence(90.0)
                    .impact("MEDIUM")
                    .recommendation("Offrir des promotions spéciales week-end pour maximiser l'occupation")
                    .build());
        }

        // Pattern 3: Clients récurrents
        Map<Long, Long> bookingsByCustomer = historicalData.stream()
                .collect(Collectors.groupingBy(Rental::getUserId, Collectors.counting()));

        long repeatCustomers = bookingsByCustomer.values().stream()
                .filter(count -> count > 1)
                .count();

        double repeatRate = (repeatCustomers * 100.0) / bookingsByCustomer.size();

        if (repeatRate > 30) {
            patterns.add(AdvancedAnalytics.Pattern.builder()
                    .patternType("CUSTOMER_BEHAVIOR")
                    .description(String.format("Taux élevé de clients récurrents: %.1f%%", repeatRate))
                    .confidence(95.0)
                    .impact("HIGH")
                    .recommendation("Lancer un programme de fidélité pour augmenter la rétention")
                    .build());
        }

        return patterns;
    }

    /**
     * Générer des recommandations stratégiques
     */
    private List<AdvancedAnalytics.Recommendation> generateRecommendations(
            List<Rental> historicalData,
            AdvancedAnalytics.TrendAnalysis trendAnalysis,
            AdvancedAnalytics.RevenueForecast forecast) {

        log.info("Génération des recommandations");
        List<AdvancedAnalytics.Recommendation> recommendations = new ArrayList<>();

        // Recommandation basée sur la tendance
        if ("UPWARD".equals(trendAnalysis.getOverallTrend())) {
            recommendations.add(AdvancedAnalytics.Recommendation.builder()
                    .category("FLEET")
                    .title("Expansion de la flotte recommandée")
                    .description("La tendance de croissance positive suggère une augmentation de la demande")
                    .priority("HIGH")
                    .potentialImpact(15.0)
                    .actionItems(Arrays.asList(
                            "Analyser les véhicules les plus demandés",
                            "Négocier l'achat ou la location de nouveaux véhicules",
                            "Évaluer les segments de marché en croissance"
                    ))
                    .build());
        }

        // Recommandation basée sur la saisonnalité
        if ("STRONG".equals(trendAnalysis.getSeasonality())) {
            recommendations.add(AdvancedAnalytics.Recommendation.builder()
                    .category("PRICING")
                    .title("Tarification dynamique saisonnière")
                    .description("Forte saisonnalité détectée - optimiser les prix selon les périodes")
                    .priority("HIGH")
                    .potentialImpact(12.0)
                    .actionItems(Arrays.asList(
                            "Augmenter les tarifs durant les mois de pic (" +
                                    String.join(", ", trendAnalysis.getPeakMonths()) + ")",
                            "Offrir des promotions durant les périodes creuses",
                            "Ajuster l'inventaire selon la demande prévue"
                    ))
                    .build());
        }

        // Recommandation basée sur la volatilité
        if (trendAnalysis.getVolatility() > 50000) {
            recommendations.add(AdvancedAnalytics.Recommendation.builder()
                    .category("OPERATIONS")
                    .title("Stabiliser les revenus")
                    .description("Volatilité élevée détectée - diversifier les sources de revenus")
                    .priority("MEDIUM")
                    .potentialImpact(10.0)
                    .actionItems(Arrays.asList(
                            "Développer des contrats de location longue durée",
                            "Créer des partenariats avec des entreprises",
                            "Offrir des services additionnels (assurance, GPS, etc.)"
                    ))
                    .build());
        }

        // Recommandation marketing
        recommendations.add(AdvancedAnalytics.Recommendation.builder()
                .category("MARKETING")
                .title("Programme de fidélisation client")
                .description("Augmenter la rétention et la valeur à vie des clients")
                .priority("HIGH")
                .potentialImpact(20.0)
                .actionItems(Arrays.asList(
                        "Lancer un programme de points de fidélité",
                        "Offrir des réductions pour les clients récurrents",
                        "Créer des offres personnalisées basées sur l'historique"
                ))
                .build());

        return recommendations;
    }

    /**
     * Analyser la performance des véhicules
     */
    private AdvancedAnalytics.VehiclePerformance analyzeVehiclePerformance(List<Rental> historicalData) {
        log.info("Analyse de la performance des véhicules");

        Map<Long, List<Rental>> rentalsByVehicle = historicalData.stream()
                .filter(r -> r.getVehicleId() != null)
                .collect(Collectors.groupingBy(Rental::getVehicleId));

        List<AdvancedAnalytics.VehicleMetrics> allVehicles = rentalsByVehicle.entrySet().stream()
                .map(entry -> {
                    Long vehicleId = entry.getKey();
                    List<Rental> rentals = entry.getValue();

                    double revenue = rentals.stream()
                            .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                            .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice().doubleValue() : 0.0)
                            .sum();

                    // Calculer le taux d'utilisation (simplifié)
                    double utilizationRate = Math.min(100, rentals.size() * 3.0); // Approximation

                    return AdvancedAnalytics.VehicleMetrics.builder()
                            .vehicleId(vehicleId)
                            .brand("Unknown") // TODO: Récupérer via VehicleClient
                            .model("Unknown")
                            .revenue(revenue)
                            .utilizationRate(utilizationRate)
                            .bookingCount(rentals.size())
                            .averageRating(4.5) // TODO: Ajouter système de notation
                            .build();
                })
                .sorted(Comparator.comparingDouble(AdvancedAnalytics.VehicleMetrics::getRevenue).reversed())
                .collect(Collectors.toList());

        List<AdvancedAnalytics.VehicleMetrics> topPerformers = allVehicles.stream()
                .limit(5)
                .collect(Collectors.toList());

        List<AdvancedAnalytics.VehicleMetrics> underPerformers = allVehicles.stream()
                .filter(v -> v.getUtilizationRate() < 30)
                .collect(Collectors.toList());

        double avgUtilization = allVehicles.stream()
                .mapToDouble(AdvancedAnalytics.VehicleMetrics::getUtilizationRate)
                .average()
                .orElse(0.0);

        String recommendation = underPerformers.isEmpty()
                ? "Flotte bien optimisée - tous les véhicules sont performants"
                : String.format("Considérer la vente ou le remplacement de %d véhicules sous-performants",
                underPerformers.size());

        return AdvancedAnalytics.VehiclePerformance.builder()
                .topPerformers(topPerformers)
                .underPerformers(underPerformers)
                .averageUtilization(Math.round(avgUtilization * 100.0) / 100.0)
                .recommendation(recommendation)
                .build();
    }

    /**
     * Segmenter les clients
     */
    private AdvancedAnalytics.CustomerSegmentation segmentCustomers(List<Rental> historicalData) {
        log.info("Segmentation des clients");

        Map<Long, List<Rental>> rentalsByCustomer = historicalData.stream()
                .collect(Collectors.groupingBy(Rental::getUserId));

        int vipCustomers = 0;
        int regularCustomers = 0;
        int newCustomers = 0;
        double vipRevenue = 0.0;
        double regularRevenue = 0.0;
        double newRevenue = 0.0;

        for (Map.Entry<Long, List<Rental>> entry : rentalsByCustomer.entrySet()) {
            List<Rental> rentals = entry.getValue();
            int bookingCount = rentals.size();

            double revenue = rentals.stream()
                    .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                    .mapToDouble(r -> r.getTotalPrice() != null ? r.getTotalPrice().doubleValue() : 0.0)
                    .sum();

            if (bookingCount > 5) {
                vipCustomers++;
                vipRevenue += revenue;
            } else if (bookingCount >= 2) {
                regularCustomers++;
                regularRevenue += revenue;
            } else {
                newCustomers++;
                newRevenue += revenue;
            }
        }

        String recommendation = String.format(
                "Focus sur la conversion des %d clients réguliers en VIP pour maximiser les revenus",
                regularCustomers);

        return AdvancedAnalytics.CustomerSegmentation.builder()
                .vipCustomers(vipCustomers)
                .regularCustomers(regularCustomers)
                .newCustomers(newCustomers)
                .vipRevenue(Math.round(vipRevenue * 100.0) / 100.0)
                .regularRevenue(Math.round(regularRevenue * 100.0) / 100.0)
                .newRevenue(Math.round(newRevenue * 100.0) / 100.0)
                .recommendation(recommendation)
                .build();
    }

    // ===== Méthodes utilitaires de calcul =====

    private double calculateWeightedMovingAverage(List<Double> values, int period) {
        if (values.isEmpty()) return 0.0;

        int size = Math.min(period, values.size());
        double sum = 0.0;
        double weightSum = 0.0;

        for (int i = 0; i < size; i++) {
            int weight = i + 1;
            sum += values.get(values.size() - size + i) * weight;
            weightSum += weight;
        }

        return weightSum > 0 ? sum / weightSum : 0.0;
    }

    private double calculateVariance(List<Double> values) {
        if (values.isEmpty()) return 0.0;

        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
    }

    private double calculateStandardDeviation(List<Double> values) {
        return Math.sqrt(calculateVariance(values));
    }

    private double calculateGrowthRate(List<Map.Entry<YearMonth, Double>> sortedMonths) {
        if (sortedMonths.size() < 2) return 0.0;

        double firstMonth = sortedMonths.get(0).getValue();
        double lastMonth = sortedMonths.get(sortedMonths.size() - 1).getValue();

        if (firstMonth == 0) return 0.0;

        return ((lastMonth - firstMonth) / firstMonth) * 100.0 / sortedMonths.size();
    }

    private String determineOverallTrend(double growthRate) {
        if (growthRate > 5) return "UPWARD";
        if (growthRate < -5) return "DOWNWARD";
        if (Math.abs(growthRate) > 10) return "VOLATILE";
        return "STABLE";
    }

    private String analyzeSeasonality(Map<YearMonth, Double> monthlyRevenue) {
        if (monthlyRevenue.size() < 6) return "INSUFFICIENT_DATA";

        double max = monthlyRevenue.values().stream().max(Double::compare).orElse(0.0);
        double min = monthlyRevenue.values().stream().min(Double::compare).orElse(0.0);
        double avg = monthlyRevenue.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        if (avg == 0) return "NONE";

        double coefficient = (max - min) / avg;

        if (coefficient > 0.5) return "STRONG";
        if (coefficient > 0.3) return "MODERATE";
        if (coefficient > 0.15) return "WEAK";
        return "NONE";
    }

    private List<String> identifyPeakMonths(Map<YearMonth, Double> monthlyRevenue) {
        double avg = monthlyRevenue.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return monthlyRevenue.entrySet().stream()
                .filter(e -> e.getValue() > avg * 1.2)
                .map(e -> e.getKey().getMonth().toString())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> identifyLowMonths(Map<YearMonth, Double> monthlyRevenue) {
        double avg = monthlyRevenue.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return monthlyRevenue.entrySet().stream()
                .filter(e -> e.getValue() < avg * 0.8)
                .map(e -> e.getKey().getMonth().toString())
                .distinct()
                .collect(Collectors.toList());
    }
}
