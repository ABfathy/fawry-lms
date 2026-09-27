package org.fathy.fawrylms.controller;

import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.enrollment.EnrollmentResponse;
import org.fathy.fawrylms.dto.payment.PaymentWebhookRequest;
import org.fathy.fawrylms.security.PaymentWebhookVerifier;
import org.fathy.fawrylms.service.EnrollmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {

    private final EnrollmentService enrollmentService;
    private final PaymentWebhookVerifier webhookVerifier;

    public PaymentWebhookController(
            EnrollmentService enrollmentService,
            PaymentWebhookVerifier webhookVerifier
    ) {
        this.enrollmentService = enrollmentService;
        this.webhookVerifier = webhookVerifier;
    }

    @PostMapping("/webhook")
    public EnrollmentResponse processPaymentWebhook(
            @RequestHeader("X-Webhook-Timestamp") String timestamp,
            @RequestHeader("X-Webhook-Signature") String signature,
            @Valid @RequestBody PaymentWebhookRequest request
    ) {
        webhookVerifier.verify(timestamp, signature, request);
        return enrollmentService.processPaymentWebHook(request);
    }
}
