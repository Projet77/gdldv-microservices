package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GDLDV-538: DTO pour les rapports financiers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport {

    private String reportId;
    private String reportType; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    private LocalDateTime generatedAt;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // Résumé financier
    private FinancialSummary summary;

    // Détails des transactions
    private List<TransactionDetail> transactions;

    // Analyse par catégorie
    private CategoryBreakdown categoryBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialSummary {
        private Double totalRevenue;
        private Double totalExpenses;
        private Double netIncome;
        private Integer totalTransactions;
        private Double averageTransactionValue;
        private Double taxAmount; // TVA collectée
        private Double projectedRevenue; // Revenu projeté basé sur les réservations futures
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDetail {
        private String transactionId;
        private LocalDateTime date;
        private String type; // RENTAL, REFUND, DAMAGE_FEE, LATE_FEE
        private String description;
        private Double amount;
        private String status; // COMPLETED, PENDING, CANCELLED
        private String customerName;
        private String vehicleInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private Double rentalRevenue;
        private Double lateFees;
        private Double damageFees;
        private Double additionalCharges;
        private Double refunds;

        // Pourcentages
        private Double rentalPercentage;
        private Double lateFeesPercentage;
        private Double damageFeesPercentage;
        private Double additionalChargesPercentage;
    }
}
