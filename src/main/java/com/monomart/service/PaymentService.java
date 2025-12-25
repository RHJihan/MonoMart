package com.monomart.service;

import com.monomart.entities.Order;
import com.monomart.entities.Payment;
import com.monomart.entities.enums.OrderStatus;
import com.monomart.entities.PaymentEvent;
import com.monomart.repository.PaymentEventRepository;
import com.monomart.repository.PaymentRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, PaymentEventRepository paymentEventRepository,
            @Lazy OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.orderService = orderService;
    }

    @Transactional
    public Payment createPayment(Order order, String transactionId, BigDecimal amount, String currency) {
        Optional<Payment> existing = paymentRepository.findByOrderId(order.getId());

        if (existing.isPresent()) {
            Payment p = existing.get();
            p.setTransactionId(transactionId);
            p.setAmount(amount);
            p.setStatus("PENDING");
            return paymentRepository.save(p);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(order.getUser());
        payment.setTransactionId(transactionId);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentSuccess(Long orderId, String paymentIntentId,
            String paymentStatus, Long amountTotal,
            String currency, String rawResponse) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No payment found for order: " + orderId));

        if ("COMPLETED".equals(payment.getStatus()))
            return;

        payment.setStatus("COMPLETED");
        payment.setTransactionId(paymentIntentId);

        if (amountTotal != null) {
            payment.setAmount(BigDecimal.valueOf(amountTotal).divide(BigDecimal.valueOf(100)));
        }

        if (currency != null) {
            payment.setCurrency(currency.toUpperCase());
        }

        paymentRepository.save(payment);

        orderService.updateStatus(orderId, OrderStatus.PAID);
    }

    @Transactional
    public void updatePaymentIntentDetails(Long orderId, String paymentIntentId,
            String status, Long amount,
            String currency, String chargeId) {

        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);

        if (paymentOpt.isEmpty())
            return;

        Payment payment = paymentOpt.get();

        if ("COMPLETED".equals(payment.getStatus()))
            return;

        payment.setTransactionId(paymentIntentId);

        if (amount != null) {
            payment.setAmount(BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100)));
        }

        if (currency != null) {
            payment.setCurrency(currency.toUpperCase());
        }

        paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentFailure(Long orderId, String rawResponse) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isEmpty())
            return;

        Payment payment = paymentOpt.get();

        if ("COMPLETED".equals(payment.getStatus()))
            return;

        payment.setStatus("FAILED");
        paymentRepository.save(payment);

        orderService.updateStatus(orderId, OrderStatus.CANCELLED);
    }

    @Transactional
    public void savePaymentEvent(String transactionId, String eventType, String payload) {
        PaymentEvent event = new PaymentEvent();
        event.setTransactionId(transactionId);
        event.setEventType(eventType);
        event.setPayload(payload);
        paymentEventRepository.save(event);
    }
}
