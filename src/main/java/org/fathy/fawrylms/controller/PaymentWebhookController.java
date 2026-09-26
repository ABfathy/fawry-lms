package org.fathy.fawrylms.controller;

import jakarta.validation.Valid;
import org.fathy.fawrylms.dto.enrollment.EnrollmentResponse;
import org.fathy.fawrylms.dto.payment.PaymentWebhookRequest;
import org.fathy.fawrylms.service.EnrollmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {

    private final EnrollmentService enrollmentService;

    public PaymentWebhookController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/webhook")
    public EnrollmentResponse processPaymentWebhook(@Valid @RequestBody PaymentWebhookRequest request) {
        return enrollmentService.processPaymentWebHook(request);
    }
}
