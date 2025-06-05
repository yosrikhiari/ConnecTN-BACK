package tn.esprit.spring.connectn.Services.CROWDFUNDING;


    import com.stripe.exception.StripeException;
    import com.stripe.model.PaymentIntent;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import tn.esprit.spring.connectn.Converters.DonationConverter;
    import tn.esprit.spring.connectn.DTO.CROWDFUNDING.DonationDTO;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Donation;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.RewardTier;
    import tn.esprit.spring.connectn.Exceptions.CROWDFUNDING.DonationNotFoundException;
    import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
    import tn.esprit.spring.connectn.Repository.CROWDFUNDING.DonationRepository;
    import tn.esprit.spring.connectn.Repository.CROWDFUNDING.RewardTierRepository;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    import static tn.esprit.spring.connectn.Services.CROWDFUNDING.CrowdfundingService.logger;

    @Service
    @RequiredArgsConstructor
    public class DonationService {


        private final DonationRepository donationRepository;
        private final StripeService stripeService;
        private final CrowdfundingRepository crowdfundingRepository;
        private final RewardTierRepository rewardTierRepository;

        @Autowired
        private DonationConverter donationConverter;
        // Create a new donation
        @Transactional
        public DonationDTO createDonation(DonationDTO donationDTO) throws StripeException {
            try {
                // 1. Verify campaign exists
                CrowdFunding campaign = crowdfundingRepository.findById(donationDTO.getCampaignId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid campaign ID"));


                // 2. Check campaign status
                if ("ACTIVE".equalsIgnoreCase(campaign.getStatus())) {
                    // If status is ACTIVE, skip date validation
                    logger.info("Campaign {} is ACTIVE, bypassing date validation", campaign.getId());
                } else {
                    // If status is not ACTIVE, use date validation
                    LocalDate today = LocalDate.now();

                    if (today.isBefore(campaign.getStartDate())) {
                        throw new IllegalStateException(String.format(
                                "Campaign hasn't started yet (starts on %s, current status: %s)",
                                campaign.getStartDate(),
                                campaign.getStatus()
                        ));
                    }

                    if (today.isAfter(campaign.getEndDate())) {
                        throw new IllegalStateException(String.format(
                                "Campaign has ended (ended on %s, current status: %s)",
                                campaign.getEndDate(),
                                campaign.getStatus()
                        ));
                    }
                }
                // 2. Process payment
                PaymentIntent intent = stripeService.createPaymentWithPaymentMethod(donationDTO);

                // 3. Create and save donation
                Donation donation = new Donation();
                donation.setUserId(donationDTO.getUserId());
                donation.setAmount(donationDTO.getAmount());
                donation.setCurrency(donationDTO.getCurrency());
                donation.setPaymentIntentId(intent.getId());
                donation.setCrowdfunding(campaign);
                donation.setAnonymous(donationDTO.isAnonymous());
                donation.setMessage(donationDTO.getMessage());
                donation.setTimestamp(LocalDateTime.now());

                // Handle reward tier if specified
                if (donationDTO.getRewardTierId() != null) {
                    RewardTier reward = rewardTierRepository.findById(donationDTO.getRewardTierId())
                            .orElse(null);
                    if (reward != null && reward.getStock() > reward.getClaimed()) {
                        donation.setRewardTier(reward);
                        reward.setClaimed(reward.getClaimed() + 1);
                        rewardTierRepository.save(reward);
                    }
                }

                Donation savedDonation = donationRepository.save(donation);

                // Update campaign amount
                campaign.setCurrentAmount(campaign.getCurrentAmount() + donation.getAmount());
                crowdfundingRepository.save(campaign);

                return donationConverter.toDto(savedDonation);

            } catch (Exception e) {
                logger.error("Donation processing failed", e);
                throw new RuntimeException("Donation processing failed: " + e.getMessage());
            }
        }

        public int countUniqueBackers(Long campaignId) {
            return donationRepository.countDistinctUserIdByCampaignId(campaignId);
        }


        // Get all donations
        public List<DonationDTO> getAllDonations() {
            List<Donation> donations = donationRepository.findAllWithRelationships();
            return donations.stream()
                    .map(donationConverter::toDto)
                    .collect(Collectors.toList());
        }
        // Get a donation by ID
        public Optional<DonationDTO> getDonationById(Long id) {
            return donationRepository.findById(id)
                    .map(donationConverter::toDto);
        }

        // Update a donation
        @Transactional
        public Donation updateDonation(Long id, Donation donationDetails) {
            Donation donation = donationRepository.findById(id)
                    .orElseThrow(() -> new DonationNotFoundException(id));

            donation.setUserId(donationDetails.getUserId());
            donation.setAmount(donationDetails.getAmount());
            donation.setCurrency(donationDetails.getCurrency());
            donation.setTimestamp(donationDetails.getTimestamp());

            return donationRepository.save(donation);
        }

        // Delete a donation
        @Transactional
        public void deleteDonation(Long id) {
            if (!donationRepository.existsById(id)) {
                throw new DonationNotFoundException(id);
            }
            donationRepository.deleteById(id);
        }

        // Refund a donation (FIXED VERSION)
        @Transactional
        public void refundDonation(Long id) throws StripeException {
            Donation donation = donationRepository.findById(id)
                    .orElseThrow(() -> new DonationNotFoundException(id));

            if (donation.getPaymentIntentId() == null || donation.getPaymentIntentId().isEmpty()) {
                throw new IllegalStateException("Donation has no payment intent associated");
            }

            if (donation.getAmount() == null || donation.getAmount() <= 0) {
                throw new IllegalStateException("Invalid donation amount");
            }

            try {
                // Process refund
                stripeService.createRefund(
                        donation.getPaymentIntentId(),
                        (long)(donation.getAmount() * 100)
                );

                // Update campaign amount
                CrowdFunding campaign = donation.getCrowdfunding();
                if (campaign != null) {
                    campaign.setCurrentAmount(campaign.getCurrentAmount() - donation.getAmount());
                    crowdfundingRepository.save(campaign);
                }

                // Remove reward claim if exists
                if (donation.getRewardTier() != null) {
                    RewardTier reward = donation.getRewardTier();
                    reward.setClaimed(reward.getClaimed() - 1);
                    rewardTierRepository.save(reward);
                }

                // Delete the donation record
                donationRepository.delete(donation);  // <-- Add this line

            } catch (StripeException e) {
                logger.error("Stripe refund failed for donation {}: {}", id, e.getMessage());
                throw new RuntimeException("Refund failed: " + e.getMessage());
            }
        }


    }