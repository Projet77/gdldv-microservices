package com.gdldv.user.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.gdldv.user.entity.CheckOut;
import com.gdldv.user.repository.CheckOutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractGenerator {

    private final CheckOutRepository checkOutRepository;

    public byte[] generateContractPDF(Long checkOutId) {
        log.info("Generating rental contract PDF for check-out: {}", checkOutId);

        CheckOut checkOut = checkOutRepository.findById(checkOutId)
            .orElseThrow(() -> new IllegalArgumentException("Check-out not found"));

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Header
            Paragraph header = new Paragraph("CONTRAT DE LOCATION",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph("\n"));

            // Company info
            Paragraph company = new Paragraph(
                "GDLDV - Gestion de Location de Véhicules\n" +
                "Adresse: Dakar, Sénégal\n" +
                "Téléphone: +221 XX XXX XX XX\n",
                FontFactory.getFont(FontFactory.HELVETICA, 10)
            );
            document.add(company);

            document.add(new Paragraph("\n"));

            // Rental info
            Paragraph info = new Paragraph(
                "INFORMATIONS DE LA LOCATION\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            );
            document.add(info);

            Paragraph details = new Paragraph(
                "Numéro de réservation: " + checkOut.getReservationId() + "\n" +
                "Date de check-out: " + checkOut.getCreatedAt().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                "Véhicule ID: " + checkOut.getVehicleId() + "\n" +
                "Client ID: " + checkOut.getUserId() + "\n\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12)
            );
            document.add(details);

            document.add(new Paragraph("\n"));

            // Vehicle initial state
            Paragraph stateHeader = new Paragraph(
                "ÉTAT INITIAL DU VÉHICULE\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            );
            document.add(stateHeader);

            Paragraph state = new Paragraph(
                "Kilométrage: " + checkOut.getMileageAtCheckOut() + " km\n" +
                "Niveau de carburant: " + checkOut.getFuelLevelAtCheckOut() + "\n" +
                "État général: " + checkOut.getVehicleConditionDescription() + "\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12)
            );
            document.add(state);

            document.add(new Paragraph("\n"));

            // Photos section
            if (checkOut.getPhotoUrls() != null && !checkOut.getPhotoUrls().isEmpty()) {
                Paragraph photos = new Paragraph(
                    "Photos de l'état initial: " + checkOut.getPhotoUrls().size() + " photo(s) enregistrée(s)\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)
                );
                document.add(photos);
                document.add(new Paragraph("\n"));
            }

            // Terms and conditions
            Paragraph termsHeader = new Paragraph(
                "CONDITIONS DE LOCATION\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            );
            document.add(termsHeader);

            Paragraph terms = new Paragraph(
                "1. Le client accepte de retourner le véhicule à la date et l'heure convenues.\n\n" +
                "2. Le client est responsable de tout dommage supplémentaire au véhicule pendant la période de location.\n\n" +
                "3. Des frais supplémentaires seront appliqués pour:\n" +
                "   - Kilométrage excédentaire (au-delà de 200 km par jour)\n" +
                "   - Essence manquante par rapport au niveau initial\n" +
                "   - Dommages non présents lors du check-out\n" +
                "   - Retard de retour du véhicule\n\n" +
                "4. Le paiement doit être complété avant la fin de la location.\n\n" +
                "5. Le client doit présenter un permis de conduire valide et une pièce d'identité.\n\n" +
                "6. L'assurance de base est incluse. Une assurance complémentaire est disponible en option.\n\n" +
                "7. Le client s'engage à utiliser le véhicule de manière responsable et conforme au code de la route.\n",
                FontFactory.getFont(FontFactory.HELVETICA, 10)
            );
            document.add(terms);

            document.add(new Paragraph("\n"));

            // Signature section
            Paragraph signatureHeader = new Paragraph(
                "SIGNATURE DU CLIENT\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)
            );
            document.add(signatureHeader);

            Paragraph signature = new Paragraph(
                "J'accepte les conditions de ce contrat de location et confirme que l'état du véhicule " +
                "décrit ci-dessus est exact.\n\n" +
                "Signature: ________________________     Date: ____/____/______\n\n" +
                "Nom complet: ______________________\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12)
            );
            document.add(signature);

            document.add(new Paragraph("\n"));

            // Footer
            Paragraph footer = new Paragraph(
                "Merci d'avoir choisi GDLDV pour votre location de véhicule!\n" +
                "En cas de problème, contactez-nous au +221 XX XXX XX XX",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            log.info("Contract PDF generated successfully");
            return outputStream.toByteArray();

        } catch (DocumentException e) {
            log.error("Error generating contract PDF", e);
            throw new RuntimeException("Failed to generate contract", e);
        }
    }
}
