package tn.esprit.spring.connectn.Exceptions.CROWDFUNDING;

public class DonationNotFoundException extends RuntimeException {
    public DonationNotFoundException(Long id) {
        super("Donation not found with id: " + id);
    }
}
