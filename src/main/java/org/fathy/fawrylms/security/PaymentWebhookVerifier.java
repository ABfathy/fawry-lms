package org.fathy.fawrylms.security;

import org.fathy.fawrylms.dto.payment.PaymentWebhookRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;

@Component
public class PaymentWebhookVerifier {

    private static final long MAX_CLOCK_SKEW_SECONDS = 300;

    private final byte[] secret;

    public PaymentWebhookVerifier(@Value("${payment.webhook-secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalStateException("PAYMENT_WEBHOOK_SECRET must contain at least 32 bytes");
        }
    }

    public void verify(String timestampHeader, String signatureHeader, PaymentWebhookRequest request) {
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            throw new AccessDeniedException("Invalid webhook timestamp");
        }

        long now = Instant.now().getEpochSecond();
        if (timestamp < now - MAX_CLOCK_SKEW_SECONDS || timestamp > now + MAX_CLOCK_SKEW_SECONDS) {
            throw new AccessDeniedException("Expired webhook timestamp");
        }

        if (signatureHeader.length() != 64) {
            throw new AccessDeniedException("Invalid webhook signature");
        }

        byte[] suppliedSignature;
        try {
            suppliedSignature = HexFormat.of().parseHex(signatureHeader);
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Invalid webhook signature");
        }

        byte[] expectedSignature = sign(timestamp, request);
        if (!MessageDigest.isEqual(expectedSignature, suppliedSignature)) {
            throw new AccessDeniedException("Invalid webhook signature");
        }
    }

    private byte[] sign(long timestamp, PaymentWebhookRequest request) {
        String payload = timestamp + "\n"
                + request.paymentReference().trim() + "\n"
                + request.status().trim().toUpperCase(Locale.ROOT);
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 is unavailable", e);
        }
    }
}
