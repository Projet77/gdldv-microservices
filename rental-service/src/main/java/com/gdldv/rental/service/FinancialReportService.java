package com.gdldv.rental.service;

import com.gdldv.rental.dto.FinancialReport;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.RentalStatus;
import com.gdldv.rental.repository.RentalRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GDLDV-538: Service de génération des rapports financiers
 * Génère des rapports PDF avec les données financières
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialReportService {

    private final RentalRepository rentalRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final double TAX_RATE = 0.18; // TVA 18%

    /**
     * Générer un rapport financier pour une période donnée
     */
    @Transactional(readOnly = true)
    public FinancialReport generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate, String reportType) {
        log.info("Génération du rapport financier {} pour la période: {} - {}", reportType, startDate, endDate);

        List<Rental> rentals = rentalRepository.findByCreatedAtBetween(startDate, endDate);

        // Calculer le résumé financier
        FinancialReport.FinancialSummary summary = calculateFinancialSummary(rentals);

        // Créer les détails des transactions
        List<FinancialReport.TransactionDetail> transactions = createTransactionDetails(rentals);

        // Calculer la répartition par catégorie
        FinancialReport.CategoryBreakdown categoryBreakdown = calculateCategoryBreakdown(rentals);

        return FinancialReport.builder()
                .reportId(UUID.randomUUID().toString())
                .reportType(reportType)
                .generatedAt(LocalDateTime.now())
                .periodStart(startDate)
                .periodEnd(endDate)
                .summary(summary)
                .transactions(transactions)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    /**
     * Générer un rapport PDF
     */
    public byte[] generatePdfReport(FinancialReport report) throws DocumentException, IOException {
        log.info("Génération du PDF pour le rapport: {}", report.getReportId());

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // En-tête du rapport
            addReportHeader(document, report);

            // Résumé financier
            addFinancialSummary(document, report.getSummary());

            // Répartition par catégorie
            addCategoryBreakdown(document, report.getCategoryBreakdown());

            // Tableau des transactions
            addTransactionsTable(document, report.getTransactions());

            // Pied de page
            addReportFooter(document, report);

            document.close();
            log.info("PDF généré avec succès: {} bytes", outputStream.size());

        } catch (DocumentException e) {
            log.error("Erreur lors de la génération du PDF: {}", e.getMessage());
            throw e;
        }

        return outputStream.toByteArray();
    }

    /**
     * Calculer le résumé financier
     */
    private FinancialReport.FinancialSummary calculateFinancialSummary(List<Rental> rentals) {
        double totalRevenue = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .mapToDouble(r -> r.getTotalAmount() != null ? r.getTotalAmount() : 0.0)
                .sum();

        double totalExpenses = 0.0; // TODO: Ajouter les dépenses si disponibles

        int totalTransactions = rentals.size();
        double averageTransactionValue = totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
        double taxAmount = totalRevenue * TAX_RATE;
        double netIncome = totalRevenue - totalExpenses - taxAmount;

        // Calculer le revenu projeté (réservations actives)
        double projectedRevenue = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.ACTIVE || r.getStatus() == RentalStatus.CHECKED_OUT)
                .mapToDouble(r -> r.getTotalAmount() != null ? r.getTotalAmount() : 0.0)
                .sum();

        return FinancialReport.FinancialSummary.builder()
                .totalRevenue(Math.round(totalRevenue * 100.0) / 100.0)
                .totalExpenses(Math.round(totalExpenses * 100.0) / 100.0)
                .netIncome(Math.round(netIncome * 100.0) / 100.0)
                .totalTransactions(totalTransactions)
                .averageTransactionValue(Math.round(averageTransactionValue * 100.0) / 100.0)
                .taxAmount(Math.round(taxAmount * 100.0) / 100.0)
                .projectedRevenue(Math.round(projectedRevenue * 100.0) / 100.0)
                .build();
    }

    /**
     * Créer les détails des transactions
     */
    private List<FinancialReport.TransactionDetail> createTransactionDetails(List<Rental> rentals) {
        return rentals.stream()
                .map(rental -> FinancialReport.TransactionDetail.builder()
                        .transactionId("TRX-" + rental.getId())
                        .date(rental.getCreatedAt())
                        .type("RENTAL")
                        .description("Location #" + rental.getId())
                        .amount(rental.getTotalAmount())
                        .status(rental.getStatus().toString())
                        .customerName("User-" + rental.getUserId())
                        .vehicleInfo("Vehicle-" + rental.getVehicleId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Calculer la répartition par catégorie
     */
    private FinancialReport.CategoryBreakdown calculateCategoryBreakdown(List<Rental> rentals) {
        double totalRevenue = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .mapToDouble(r -> r.getTotalAmount() != null ? r.getTotalAmount() : 0.0)
                .sum();

        // Pour l'instant, tout le revenu vient des locations
        // TODO: Ajouter la logique pour séparer les différents types de revenus
        double rentalRevenue = totalRevenue;
        double lateFees = 0.0;
        double damageFees = 0.0;
        double additionalCharges = 0.0;
        double refunds = 0.0;

        return FinancialReport.CategoryBreakdown.builder()
                .rentalRevenue(Math.round(rentalRevenue * 100.0) / 100.0)
                .lateFees(Math.round(lateFees * 100.0) / 100.0)
                .damageFees(Math.round(damageFees * 100.0) / 100.0)
                .additionalCharges(Math.round(additionalCharges * 100.0) / 100.0)
                .refunds(Math.round(refunds * 100.0) / 100.0)
                .rentalPercentage(totalRevenue > 0 ? 100.0 : 0.0)
                .lateFeesPercentage(0.0)
                .damageFeesPercentage(0.0)
                .additionalChargesPercentage(0.0)
                .build();
    }

    /**
     * Ajouter l'en-tête du rapport PDF
     */
    private void addReportHeader(Document document, FinancialReport report) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph title = new Paragraph("RAPPORT FINANCIER", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph subtitle = new Paragraph(
                String.format("Type: %s | Période: %s - %s",
                        report.getReportType(),
                        report.getPeriodStart().format(DATE_FORMATTER),
                        report.getPeriodEnd().format(DATE_FORMATTER)),
                subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
    }

    /**
     * Ajouter le résumé financier au PDF
     */
    private void addFinancialSummary(Document document, FinancialReport.FinancialSummary summary) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("RÉSUMÉ FINANCIER", sectionFont);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        addTableRow(table, "Revenu Total", formatCurrency(summary.getTotalRevenue()), true);
        addTableRow(table, "Dépenses", formatCurrency(summary.getTotalExpenses()), false);
        addTableRow(table, "TVA (18%)", formatCurrency(summary.getTaxAmount()), false);
        addTableRow(table, "Revenu Net", formatCurrency(summary.getNetIncome()), true);
        addTableRow(table, "Nombre de Transactions", String.valueOf(summary.getTotalTransactions()), false);
        addTableRow(table, "Valeur Moyenne Transaction", formatCurrency(summary.getAverageTransactionValue()), false);
        addTableRow(table, "Revenu Projeté", formatCurrency(summary.getProjectedRevenue()), false);

        document.add(table);
    }

    /**
     * Ajouter la répartition par catégorie au PDF
     */
    private void addCategoryBreakdown(Document document, FinancialReport.CategoryBreakdown breakdown) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("RÉPARTITION PAR CATÉGORIE", sectionFont);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        // En-tête
        addTableHeader(table, "Catégorie");
        addTableHeader(table, "Montant");
        addTableHeader(table, "Pourcentage");

        // Données
        addTableRow(table, "Locations", formatCurrency(breakdown.getRentalRevenue()), breakdown.getRentalPercentage() + "%");
        addTableRow(table, "Frais de Retard", formatCurrency(breakdown.getLateFees()), breakdown.getLateFeesPercentage() + "%");
        addTableRow(table, "Frais de Dommages", formatCurrency(breakdown.getDamageFees()), breakdown.getDamageFeesPercentage() + "%");
        addTableRow(table, "Frais Supplémentaires", formatCurrency(breakdown.getAdditionalCharges()), breakdown.getAdditionalChargesPercentage() + "%");
        addTableRow(table, "Remboursements", formatCurrency(breakdown.getRefunds()), "-");

        document.add(table);
    }

    /**
     * Ajouter le tableau des transactions au PDF
     */
    private void addTransactionsTable(Document document, List<FinancialReport.TransactionDetail> transactions) throws DocumentException {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("DÉTAIL DES TRANSACTIONS", sectionFont);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        // En-tête
        addTableHeader(table, "Date");
        addTableHeader(table, "Type");
        addTableHeader(table, "Description");
        addTableHeader(table, "Montant");
        addTableHeader(table, "Statut");

        // Données (limiter à 20 transactions pour ne pas surcharger le PDF)
        transactions.stream()
                .limit(20)
                .forEach(txn -> {
                    addTableRow(table, txn.getDate().format(DATE_FORMATTER), txn.getType(),
                            txn.getDescription(), formatCurrency(txn.getAmount()), txn.getStatus());
                });

        if (transactions.size() > 20) {
            Paragraph note = new Paragraph(
                    String.format("Note: Seules les 20 premières transactions sont affichées (%d au total)",
                            transactions.size()),
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
            note.setSpacingBefore(10);
            document.add(note);
        }

        document.add(table);
    }

    /**
     * Ajouter le pied de page au PDF
     */
    private void addReportFooter(Document document, FinancialReport report) throws DocumentException {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        Paragraph footer = new Paragraph(
                String.format("Rapport généré le %s | ID: %s",
                        report.getGeneratedAt().format(DATE_FORMATTER),
                        report.getReportId()),
                footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }

    /**
     * Ajouter une ligne au tableau PDF
     */
    private void addTableRow(PdfPTable table, String label, String value, boolean bold) {
        Font font = bold ? new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)
                : new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    /**
     * Ajouter une ligne au tableau PDF (3 colonnes)
     */
    private void addTableRow(PdfPTable table, String col1, String col2, String col3) {
        Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

        table.addCell(new Phrase(col1, font));
        table.addCell(new Phrase(col2, font));
        table.addCell(new Phrase(col3, font));
    }

    /**
     * Ajouter une ligne au tableau PDF (5 colonnes)
     */
    private void addTableRow(PdfPTable table, String col1, String col2, String col3, String col4, String col5) {
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

        table.addCell(new Phrase(col1, font));
        table.addCell(new Phrase(col2, font));
        table.addCell(new Phrase(col3, font));
        table.addCell(new Phrase(col4, font));
        table.addCell(new Phrase(col5, font));
    }

    /**
     * Ajouter un en-tête de colonne au tableau PDF
     */
    private void addTableHeader(PdfPTable table, String headerTitle) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell header = new PdfPCell(new Phrase(headerTitle, headerFont));
        header.setBackgroundColor(BaseColor.DARK_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(5);
        table.addCell(header);
    }

    /**
     * Formater un montant en devise
     */
    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0 FCFA";
        }
        return String.format("%,.0f FCFA", amount);
    }
}
