package org.fathy.fawrylms.exception;

public class IncorrectPaymentStatusException extends RuntimeException {
    public IncorrectPaymentStatusException(String message) {
        super(message);
    }
}
