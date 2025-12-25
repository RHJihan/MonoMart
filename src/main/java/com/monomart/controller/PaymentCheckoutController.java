package com.monomart.controller;

import com.monomart.dto.payment.CheckoutRequest;
import com.monomart.dto.payment.ProductRequest;
import com.monomart.dto.payment.StripeResponse;
import com.monomart.entities.Order;
import com.monomart.security.AuthenticatedUser;
import com.monomart.service.OrderService;
import com.monomart.service.StripeService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentCheckoutController {

    private StripeService stripeService;

    private OrderService orderService;
    private com.monomart.service.PaymentService paymentService;

    public PaymentCheckoutController(StripeService stripeService, OrderService orderService,
            com.monomart.service.PaymentService paymentService) {
        this.stripeService = stripeService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    private Long currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au)
            return au.getUserId();
        throw new IllegalStateException("Invalid principal");
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(
            @RequestBody CheckoutRequest request, Authentication auth) {
        Long orderId = request.getOrderId();
        if (orderId == null)
            throw new IllegalArgumentException("orderId is required");

        Long userId = currentUserId(auth);
        Order order = orderService.getOrderForCheckout(orderId, userId);

        // Convert BigDecimal totalAmount to Long cents (assuming USD)
        // Note: This simple conversion assumes 2 decimal places.
        // Ideally should handle currency specific formatting.
        Long amountInCents = order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue();

        ProductRequest productRequest = new ProductRequest(
                amountInCents,
                "USD", // currency
                orderId);

        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);

        paymentService.createPayment(
                order,
                stripeResponse.getSessionId(),
                order.getTotalAmount(),
                "USD");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
}
