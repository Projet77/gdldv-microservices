package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationStatus;
import com.gdldv.reservation.repository.ReservationRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final ReservationRepository reservationRepository;
    private final VehicleClient vehicleClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateInvoicePdf(Long reservationId) {
        log.info("Generating invoice PDF for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED &&
            reservation.getStatus() != ReservationStatus.ACTIVE &&
            reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new RuntimeException("Cannot generate invoice for reservation with status: " + reservation.getStatus());
        }

        VehicleDTO vehicle = vehicleClient.getVehicleById(reservation.getVehicleId());

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // En-tête de la facture
            addInvoiceHeader(document, reservation);

            // Informations client et réservation
            addCustomerInfo(document, reservation);

            // Détails du véhicule et de la location
            addRentalDetails(document, reservation, vehicle);

            // Total
            addTotal(document, reservation);

            // Pied de page
            addFooter(document);

            document.close();

            log.info("Invoice PDF generated successfully for reservation: {}", reservationId);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating invoice PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addInvoiceHeader(Document document, Reservation reservation) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("FACTURE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);
        Paragraph subtitle = new Paragraph("GDLDV - Gestion de Location de Véhicules", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        Paragraph invoiceNumber = new Paragraph("Facture N° " + String.format("INV-%06d", reservation.getId()));
        invoiceNumber.setSpacingAfter(5);
        document.add(invoiceNumber);

        Paragraph invoiceDate = new Paragraph("Date: " + LocalDate.now().format(DATE_FORMATTER));
        invoiceDate.setSpacingAfter(20);
        document.add(invoiceDate);
    }

    private void addCustomerInfo(Document document, Reservation reservation) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        Paragraph clientHeader = new Paragraph("Informations Client", headerFont);
        clientHeader.setSpacingBefore(10);
        document.add(clientHeader);

        Paragraph clientId = new Paragraph("Client ID: " + reservation.getUserId());
        clientId.setSpacingAfter(15);
        document.add(clientId);
    }

    private void addRentalDetails(Document document, Reservation reservation, VehicleDTO vehicle) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        Paragraph detailsHeader = new Paragraph("Détails de la Location", headerFont);
        detailsHeader.setSpacingBefore(10);
        detailsHeader.setSpacingAfter(10);
        document.add(detailsHeader);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(15);

        // En-têtes de colonnes
        Font cellHeaderFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell1 = new PdfPCell(new Phrase("Description", cellHeaderFont));
        headerCell1.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell1.setPadding(5);
        table.addCell(headerCell1);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Détails", cellHeaderFont));
        headerCell2.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell2.setPadding(5);
        table.addCell(headerCell2);

        // Données
        addTableRow(table, "Véhicule", vehicle.getBrand() + " " + vehicle.getModel());
        addTableRow(table, "Immatriculation", vehicle.getLicensePlate());
        addTableRow(table, "Catégorie", vehicle.getCategory());
        addTableRow(table, "Date de début", reservation.getStartDate().format(DATE_FORMATTER));
        addTableRow(table, "Date de fin", reservation.getEndDate().format(DATE_FORMATTER));

        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        addTableRow(table, "Nombre de jours", String.valueOf(days));
        addTableRow(table, "Prix par jour", String.format("%.0f XOF", vehicle.getDailyPrice()));

        document.add(table);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        labelCell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(5);
        valueCell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(valueCell);
    }

    private void addTotal(Document document, Reservation reservation) throws DocumentException {
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        Paragraph total = new Paragraph(
                String.format("TOTAL: %.0f XOF", reservation.getTotalPrice()),
                totalFont
        );
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(10);
        total.setSpacingAfter(20);
        document.add(total);
    }

    private void addFooter(Document document) throws DocumentException {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

        Paragraph footer = new Paragraph(
                "Merci pour votre confiance!\n" +
                "GDLDV - Gestion de Location de Véhicules\n" +
                "Contact: contact@gdldv.com | Tél: +221 XX XXX XX XX",
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
}
