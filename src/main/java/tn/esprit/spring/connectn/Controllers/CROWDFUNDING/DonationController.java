package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


    import com.stripe.exception.StripeException;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.BindingResult;
    import org.springframework.validation.FieldError;
    import org.springframework.web.bind.annotation.*;
    import tn.esprit.spring.connectn.DTO.CROWDFUNDING.DonationDTO;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Donation;
    import tn.esprit.spring.connectn.Services.CROWDFUNDING.CrowdfundingService;
    import tn.esprit.spring.connectn.Services.CROWDFUNDING.DonationService;

    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    import static tn.esprit.spring.connectn.Services.CROWDFUNDING.CrowdfundingService.logger;

    @RestController
    @RequestMapping("/api/donations")
    public class DonationController {

        @Autowired
        private DonationService donationService;
        private CrowdfundingService crowdfundingService;

        @PostMapping
        public ResponseEntity<?> createDonation(@RequestBody @Valid DonationDTO donationDTO, BindingResult result) {
            if (result.hasErrors()) {
                Map<String, String> errors = result.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage
                        ));
                return ResponseEntity.badRequest().body(errors);
            }

            try {
                DonationDTO createdDonation = donationService.createDonation(donationDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdDonation);
            } catch (StripeException e) {
                logger.error("Stripe error during donation", e);
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(Map.of(
                                "error", "Payment processing failed",
                                "details", e.getMessage()
                        ));
            } catch (Exception e) {
                logger.error("Donation processing failed", e);
                return ResponseEntity.internalServerError()
                        .body(Map.of(
                                "error", "Donation processing failed",
                                "details", e.getMessage()
                        ));
            }
        }


        // Get all donations

        @GetMapping
        public List<DonationDTO> getAllDonations() {
            return donationService.getAllDonations();
        }


        // Get a donation by ID
        @GetMapping("/{id}")
        public ResponseEntity<DonationDTO> getDonationById(@PathVariable Long id) {
            return donationService.getDonationById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // Update a donation
        @PutMapping("/{id}")
        public ResponseEntity<Donation> updateDonation(@PathVariable Long id, @RequestBody Donation donationDetails) {
            Donation updatedDonation = donationService.updateDonation(id, donationDetails);
            return ResponseEntity.ok(updatedDonation);
        }

        // Delete a donation
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
            donationService.deleteDonation(id);
            return ResponseEntity.noContent().build();
        }

        // Refund a donation
        @PostMapping("/{id}/refund")
        public ResponseEntity<Map<String, String>> refundDonation(@PathVariable Long id) {
            try {
                donationService.refundDonation(id);
                return ResponseEntity.ok(Map.of("message", "Refund processed successfully"));
            } catch (Exception e) {
                logger.error("Refund failed for donation {}: {}", id, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Refund failed: " + e.getMessage()));
            }
        }




        @GetMapping("/unique-backers")
        public ResponseEntity<Integer> getUniqueBackersCount(@PathVariable Long campaignId) {
            int uniqueBackers = donationService.countUniqueBackers(campaignId);
            return ResponseEntity.ok(uniqueBackers);
        }




    }