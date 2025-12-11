package com.gdldv.reservation.controller;

import com.gdldv.reservation.service.InvoiceService;
import com.gdldv.reservation.service.PaymentService;
import com.gdldv.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, Object> request) {
        try {
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            String customerEmail = request.get("email").toString();

            log.info("Creating checkout session for reservation: {}", reservationId);

            String clientSecret = reservationService.initializePayment(reservationId, customerEmail);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", clientSecret,
                    "message", "Payment session created successfully"
            ));

        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");

            log.info("Confirming payment: {}", paymentIntentId);

            reservationService.confirmPayment(paymentIntentId);

            return ResponseEntity.ok(Map.of(
                    "message", "Payment confirmed successfully"
            ));

        } catch (Exception e) {
            log.error("Error confirming payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            log.info("Received Stripe webhook");

            // Pour l'instant, traitement basique
            // Dans une vraie implémentation, il faut valider la signature Stripe
            paymentService.handleWebhookEvent("payment_intent.succeeded", payload);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error handling webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId").toString();
            Double amount = Double.valueOf(request.get("amount").toString());

            log.info("Processing refund for payment: {}", paymentIntentId);

            String refundId = paymentService.refundPayment(paymentIntentId, amount);

            return ResponseEntity.ok(Map.of(
                    "refundId", refundId,
                    "message", "Refund processed successfully"
            ));

        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/invoice/{reservationId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long reservationId) {
        try {
            log.info("Generating invoice for reservation: {}", reservationId);

            byte[] pdfBytes = invoiceService.generateInvoicePdf(reservationId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    String.format("facture-reservation-%d.pdf", reservationId));
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error generating invoice: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
