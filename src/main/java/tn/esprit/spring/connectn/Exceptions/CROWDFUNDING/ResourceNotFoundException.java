package tn.esprit.spring.connectn.Exceptions.CROWDFUNDING;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
