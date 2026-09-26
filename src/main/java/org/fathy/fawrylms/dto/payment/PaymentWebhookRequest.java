package org.fathy.fawrylms.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentWebhookRequest(@NotBlank String paymentReference, @NotBlank String status) {
}
