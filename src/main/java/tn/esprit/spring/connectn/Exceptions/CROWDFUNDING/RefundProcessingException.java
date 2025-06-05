package tn.esprit.spring.connectn.Exceptions.CROWDFUNDING;

public class RefundProcessingException extends RuntimeException {
    public RefundProcessingException(String message) {
        super(message);
    }
    public RefundProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}