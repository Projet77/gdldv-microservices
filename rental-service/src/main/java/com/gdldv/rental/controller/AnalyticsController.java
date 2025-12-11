package com.gdldv.rental.controller;

import com.gdldv.rental.dto.AdvancedAnalytics;
import com.gdldv.rental.dto.ApiResponse;
import com.gdldv.rental.dto.BusinessMetrics;
import com.gdldv.rental.dto.FinancialReport;
import com.gdldv.rental.service.AdvancedAnalyticsService;
import com.gdldv.rental.service.BusinessMetricsService;
import com.gdldv.rental.service.FinancialReportService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GDLDV-533: KPI Dashboard Controller
 * Endpoints pour récupérer les métriques métier et KPIs
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "KPI Dashboard et métriques métier")
public class AnalyticsController {

    private final BusinessMetricsService businessMetricsService;
    private final FinancialReportService financialReportService;
    private final AdvancedAnalyticsService advancedAnalyticsService;

    /**
     * GDLDV-533: Récupérer les KPIs du jour
     */
    @GetMapping("/kpi/today")
    @Operation(summary = "Métriques du jour", description = "Récupère les KPIs pour la journée en cours")
    public ResponseEntity<ApiResponse<BusinessMetrics>> getTodayKPIs() {
        log.info("Récupération des KPIs du jour");
        BusinessMetrics metrics = businessMetricsService.getTodayMetrics();

        return ResponseEntity.ok(ApiResponse.<BusinessMetrics>builder()
                .success(true)
                .message("KPIs du jour récupérés avec succès")
                .data(metrics)
                .build());
    }

    /**
     * GDLDV-533: Récupérer les KPIs du mois
     */
    @GetMapping("/kpi/month")
    @Operation(summary = "Métriques du mois", description = "Récupère les KPIs pour le mois en cours")
    public ResponseEntity<ApiResponse<BusinessMetrics>> getMonthKPIs() {
        log.info("Récupération des KPIs du mois");
        BusinessMetrics metrics = businessMetricsService.getMonthMetrics();

        return ResponseEntity.ok(ApiResponse.<BusinessMetrics>builder()
                .success(true)
                .message("KPIs du mois récupérés avec succès")
                .data(metrics)
                .build());
    }

    /**
     * GDLDV-533: Récupérer les KPIs de l'année
     */
    @GetMapping("/kpi/year")
    @Operation(summary = "Métriques de l'année", description = "Récupère les KPIs pour l'année en cours")
    public ResponseEntity<ApiResponse<BusinessMetrics>> getYearKPIs() {
        log.info("Récupération des KPIs de l'année");
        BusinessMetrics metrics = businessMetricsService.getYearMetrics();

        return ResponseEntity.ok(ApiResponse.<BusinessMetrics>builder()
                .success(true)
                .message("KPIs de l'année récupérés avec succès")
                .data(metrics)
                .build());
    }

    /**
     * GDLDV-533: Récupérer les KPIs pour une période personnalisée
     */
    @GetMapping("/kpi/custom")
    @Operation(summary = "Métriques personnalisées", description = "Récupère les KPIs pour une période définie")
    public ResponseEntity<ApiResponse<BusinessMetrics>> getCustomKPIs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("Récupération des KPIs pour la période: {} - {}", start, end);

        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body(ApiResponse.<BusinessMetrics>builder()
                    .success(false)
                    .message("La date de début doit être antérieure à la date de fin")
                    .build());
        }

        BusinessMetrics metrics = businessMetricsService.getCustomPeriodMetrics(start, end);

        return ResponseEntity.ok(ApiResponse.<BusinessMetrics>builder()
                .success(true)
                .message("KPIs personnalisés récupérés avec succès")
                .data(metrics)
                .build());
    }

    /**
     * GDLDV-538: Générer un rapport financier (JSON)
     */
    @GetMapping("/reports/financial")
    @Operation(summary = "Rapport financier", description = "Génère un rapport financier pour une période donnée")
    public ResponseEntity<ApiResponse<FinancialReport>> generateFinancialReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "CUSTOM") String reportType) {
        log.info("Génération du rapport financier {} pour la période: {} - {}", reportType, start, end);

        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body(ApiResponse.<FinancialReport>builder()
                    .success(false)
                    .message("La date de début doit être antérieure à la date de fin")
                    .build());
        }

        FinancialReport report = financialReportService.generateFinancialReport(start, end, reportType);

        return ResponseEntity.ok(ApiResponse.<FinancialReport>builder()
                .success(true)
                .message("Rapport financier généré avec succès")
                .data(report)
                .build());
    }

    /**
     * GDLDV-538: Télécharger un rapport financier en PDF
     */
    @GetMapping("/reports/financial/pdf")
    @Operation(summary = "Rapport financier PDF", description = "Génère et télécharge un rapport financier en PDF")
    public ResponseEntity<byte[]> downloadFinancialReportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "CUSTOM") String reportType) {
        log.info("Génération du PDF du rapport financier {} pour la période: {} - {}", reportType, start, end);

        try {
            if (start.isAfter(end)) {
                return ResponseEntity.badRequest().build();
            }

            // Générer le rapport
            FinancialReport report = financialReportService.generateFinancialReport(start, end, reportType);

            // Générer le PDF
            byte[] pdfBytes = financialReportService.generatePdfReport(report);

            // Créer le nom du fichier
            String filename = String.format("rapport_financier_%s_%s.pdf",
                    reportType.toLowerCase(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (DocumentException | IOException e) {
            log.error("Erreur lors de la génération du PDF: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GDLDV-538: Rapports prédéfinis - Journalier
     */
    @GetMapping("/reports/daily")
    @Operation(summary = "Rapport journalier", description = "Génère le rapport financier du jour")
    public ResponseEntity<ApiResponse<FinancialReport>> getDailyReport() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        FinancialReport report = financialReportService.generateFinancialReport(startOfDay, endOfDay, "DAILY");

        return ResponseEntity.ok(ApiResponse.<FinancialReport>builder()
                .success(true)
                .message("Rapport journalier généré avec succès")
                .data(report)
                .build());
    }

    /**
     * GDLDV-538: Rapports prédéfinis - Mensuel
     */
    @GetMapping("/reports/monthly")
    @Operation(summary = "Rapport mensuel", description = "Génère le rapport financier du mois")
    public ResponseEntity<ApiResponse<FinancialReport>> getMonthlyReport() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).minusDays(1)
                .withHour(23).withMinute(59).withSecond(59);

        FinancialReport report = financialReportService.generateFinancialReport(startOfMonth, endOfMonth, "MONTHLY");

        return ResponseEntity.ok(ApiResponse.<FinancialReport>builder()
                .success(true)
                .message("Rapport mensuel généré avec succès")
                .data(report)
                .build());
    }

    /**
     * GDLDV-538: Rapports prédéfinis - Annuel
     */
    @GetMapping("/reports/yearly")
    @Operation(summary = "Rapport annuel", description = "Génère le rapport financier de l'année")
    public ResponseEntity<ApiResponse<FinancialReport>> getYearlyReport() {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfYear = LocalDateTime.now().withMonth(12).withDayOfMonth(31)
                .withHour(23).withMinute(59).withSecond(59);

        FinancialReport report = financialReportService.generateFinancialReport(startOfYear, endOfYear, "YEARLY");

        return ResponseEntity.ok(ApiResponse.<FinancialReport>builder()
                .success(true)
                .message("Rapport annuel généré avec succès")
                .data(report)
                .build());
    }

    /**
     * GDLDV-542: Analytics avancés avec prédictions et recommandations
     */
    @GetMapping("/advanced")
    @Operation(summary = "Analytics avancés", description = "Génère des prédictions, tendances et recommandations")
    public ResponseEntity<ApiResponse<AdvancedAnalytics>> getAdvancedAnalytics() {
        log.info("Génération des analytics avancés");

        AdvancedAnalytics analytics = advancedAnalyticsService.generateAdvancedAnalytics();

        return ResponseEntity.ok(ApiResponse.<AdvancedAnalytics>builder()
                .success(true)
                .message("Analytics avancés générés avec succès")
                .data(analytics)
                .build());
    }

    /**
     * GDLDV-542: Prédictions de revenus uniquement
     */
    @GetMapping("/forecast")
    @Operation(summary = "Prédictions de revenus", description = "Génère les prédictions de revenus futurs")
    public ResponseEntity<ApiResponse<AdvancedAnalytics.RevenueForecast>> getRevenueForecast() {
        log.info("Génération des prédictions de revenus");

        AdvancedAnalytics analytics = advancedAnalyticsService.generateAdvancedAnalytics();

        return ResponseEntity.ok(ApiResponse.<AdvancedAnalytics.RevenueForecast>builder()
                .success(true)
                .message("Prédictions de revenus générées avec succès")
                .data(analytics.getRevenueForecast())
                .build());
    }

    /**
     * GDLDV-542: Recommandations stratégiques
     */
    @GetMapping("/recommendations")
    @Operation(summary = "Recommandations", description = "Génère des recommandations stratégiques basées sur les données")
    public ResponseEntity<ApiResponse<List<AdvancedAnalytics.Recommendation>>> getRecommendations() {
        log.info("Génération des recommandations");

        AdvancedAnalytics analytics = advancedAnalyticsService.generateAdvancedAnalytics();

        return ResponseEntity.ok(ApiResponse.<List<AdvancedAnalytics.Recommendation>>builder()
                .success(true)
                .message("Recommandations générées avec succès")
                .data(analytics.getRecommendations())
                .build());
    }
}
