package com.monomart.controller;

import com.monomart.service.PaymentService;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@Tag(name = "Stripe Webhook", description = "Endpoint to receive Stripe payment events")
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhookSecret}")
    private String endpointSecret;

    public StripeWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    @SecurityRequirements({})
    @Operation(summary = "Stripe Webhook", description = "Receive Stripe events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook received successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or signature")
    })
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid Signature");
        }

        String eventType = event.getType();
        System.out.println("⚡ Stripe Event: " + eventType);

        // Extract transaction ID (PaymentIntent ID) from the event
        String transactionId = extractTransactionId(event);

        // Extract order ID to ensure mapping exists
        Long orderIdFromEvent = extractOrderId(event);
        if (orderIdFromEvent != null && transactionId != null) {
            paymentService.ensurePaymentMapping(orderIdFromEvent, transactionId);
        }

        // Save the event to PaymentEvent table
        paymentService.savePaymentEvent(transactionId, eventType, payload);

        switch (eventType) {

            // Checkout
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event, payload);
                break;

            case "checkout.session.expired":
                handleCheckoutExpired(event);
                break;

            // Payment Intent
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;

            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event, payload);
                break;

            case "payment_intent.canceled":
                handlePaymentIntentCanceled(event);
                break;

            // Charges
            case "charge.succeeded":
                handleChargeSucceeded(event);
                break;

            case "charge.failed":
                handleChargeFailed(event);
                break;
            case "charge.updated":
                handleChargeUpdated(event);
                break;

            default:
                System.out.println("👉 Unhandled Event Type: " + eventType);
        }

        return ResponseEntity.ok("Received");
    }

    private void handleCheckoutSessionCompleted(Event event, String payload) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null)
            return;

        String orderIdStr = session.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);

            paymentService.updatePaymentSuccess(
                    orderId,
                    session.getPaymentIntent(),
                    session.getPaymentStatus(),
                    session.getAmountTotal(),
                    session.getCurrency(),
                    payload);

        } catch (RuntimeException ignored) {
        }
    }

    private void handleCheckoutExpired(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null)
            return;

        String orderIdStr = session.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);
            paymentService.updatePaymentFailure(orderId, "Checkout session expired");
        } catch (RuntimeException ignored) {
        }
    }

    private void handlePaymentIntentFailed(Event event, String payload) {
        PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (failedIntent == null)
            return;

        String orderIdStr = failedIntent.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);
            paymentService.updatePaymentFailure(orderId, payload);
        } catch (RuntimeException ignored) {
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (intent == null)
            return;

        String orderIdStr = intent.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);

            paymentService.updatePaymentIntentDetails(
                    orderId,
                    intent.getId(),
                    intent.getStatus(),
                    intent.getAmount(),
                    intent.getCurrency(),
                    intent.getLatestCharge());
        } catch (RuntimeException ignored) {
        }
    }

    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (intent == null)
            return;

        String orderIdStr = intent.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);
            paymentService.updatePaymentFailure(orderId, "Payment Canceled");
        } catch (RuntimeException ignored) {
        }
    }

    private void handleChargeSucceeded(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (charge == null)
            return;

        System.out.println("Charge Succeeded: " + charge.getId());
    }

    private void handleChargeFailed(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (charge == null)
            return;

        System.out.println("Charge Failed: " + charge.getFailureMessage());
    }

    private void handleChargeUpdated(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (charge == null)
            return;

        System.out.println("Charge Updated: " + charge.getId());

        // If you want to bind it to your order:
        String orderIdStr = charge.getMetadata().get("order_id");
        if (orderIdStr == null)
            return;

        try {
            Long orderId = Long.parseLong(orderIdStr);

            // Example: if charge became refunded
            if ("refunded".equals(charge.getStatus())) {
                paymentService.updatePaymentFailure(orderId, "Charge refunded");
            }

            // Example: if risk was flagged / blocked
            if (charge.getFraudDetails() != null) {
                System.out.println("Fraud detected");
            }

            // Or just store provider update
            // paymentService.updateProviderResponse(orderId, charge.toJson());
        } catch (RuntimeException ignored) {
        }
    }

    /**
     * Extract order ID from different event types
     */
    private Long extractOrderId(Event event) {
        try {
            Map<String, String> metadata = null;
            Object obj = event.getDataObjectDeserializer().getObject().orElse(null);

            if (obj instanceof Session session) {
                metadata = session.getMetadata();
            } else if (obj instanceof PaymentIntent intent) {
                metadata = intent.getMetadata();
            } else if (obj instanceof Charge charge) {
                metadata = charge.getMetadata();
            }

            if (metadata != null) {
                String orderIdStr = metadata.get("order_id");
                if (orderIdStr != null) {
                    return Long.parseLong(orderIdStr);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract transaction ID (PaymentIntent ID) from different event types
     */
    private String extractTransactionId(Event event) {
        try {
            String eventType = event.getType();

            // For PaymentIntent events
            if (eventType.startsWith("payment_intent.")) {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);
                return intent != null ? intent.getId() : null;
            }

            // For Checkout Session events
            if (eventType.startsWith("checkout.session.")) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);
                return session != null ? session.getPaymentIntent() : null;
            }

            // For Charge events
            if (eventType.startsWith("charge.")) {
                Charge charge = (Charge) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);
                return charge != null ? charge.getPaymentIntent() : null;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
