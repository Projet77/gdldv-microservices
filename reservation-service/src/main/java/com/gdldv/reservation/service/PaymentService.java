package com.gdldv.reservation.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createCheckoutSession(Long reservationId, Double amount, String email) {
        log.info("Creating Stripe checkout session for reservation: {}", reservationId);

        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // Convert to cents
                .setCurrency("xof") // CFA Franc
                .setReceiptEmail(email)
                .putMetadata("reservationId", reservationId.toString())
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("Payment Intent created: {}", paymentIntent.getId());
            return paymentIntent.getClientSecret();

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }

    public boolean confirmPayment(String paymentIntentId) {
        log.info("Confirming payment: {}", paymentIntentId);

        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            boolean isSucceeded = "succeeded".equals(paymentIntent.getStatus());
            log.info("Payment status: {}", paymentIntent.getStatus());

            return isSucceeded;

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            return false;
        }
    }

    public String refundPayment(String paymentIntentId, Double amount) {
        log.info("Refunding payment: {} for amount: {}", paymentIntentId, amount);

        try {
            Stripe.apiKey = stripeApiKey;

            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .setAmount((long) (amount * 100)) // Convert to cents
                .build();

            Refund refund = Refund.create(params);

            log.info("Refund created: {}", refund.getId());
            return refund.getId();

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            throw new RuntimeException("Failed to refund payment", e);
        }
    }

    public void handleWebhookEvent(String event, String data) {
        log.info("Handling Stripe webhook event: {}", event);

        if ("payment_intent.succeeded".equals(event)) {
            // Marquer réservation comme CONFIRMED
            // Générer la facture
            log.info("Payment succeeded - marking reservation as CONFIRMED");
        } else if ("payment_intent.payment_failed".equals(event)) {
            // Marquer réservation comme PENDING
            log.info("Payment failed - marking reservation as PENDING");
        }
    }
}
