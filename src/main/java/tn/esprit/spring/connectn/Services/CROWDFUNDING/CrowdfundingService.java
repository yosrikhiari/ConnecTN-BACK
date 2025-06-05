package tn.esprit.spring.connectn.Services.CROWDFUNDING;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stripe.exception.StripeException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import tn.esprit.spring.connectn.Converters.CampaignConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CrowdFundingDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignAnalytics;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Exceptions.CROWDFUNDING.CrowdfundingNotFoundException;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class CrowdfundingService {

    public static final Logger logger = LoggerFactory.getLogger(CrowdfundingService.class);

    @Autowired
    private CampaignConverter campaignConverter;

    @Autowired
    private CrowdfundingRepository crowdfundingRepository;

    @Autowired
    private CampaignAnalyticsRepository analyticsRepository;

    @Autowired
    private CampaignUpdateRepository updateRepository;


    @Transactional
    public CrowdFunding createCrowdfunding(@Valid CrowdFunding crowdfunding, Long userId) {
        // Validate user exists
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Set creator and default values
        crowdfunding.setCreatedBy(userId);
        crowdfunding.setCreatedAt(LocalDateTime.now());
        crowdfunding.setCurrentAmount(0.0);

        // Handle dates
        LocalDate today = LocalDate.now();
        if (crowdfunding.getStartDate() == null) {
            crowdfunding.setStartDate(today);
        }

        // Set status
        if (crowdfunding.getStatus() == null) {
            crowdfunding.setStatus(crowdfunding.getStartDate().isAfter(today) ? "DRAFT" : "ACTIVE");
        }

        // Save campaign first to get ID
        CrowdFunding savedCampaign = crowdfundingRepository.save(crowdfunding);

        // Handle analytics
        if (crowdfunding.getAnalytics() != null) {
            CampaignAnalytics analytics = crowdfunding.getAnalytics();
            analytics.setCampaign(savedCampaign);

            // Initialize maps if null
            if (analytics.getDailyViews() == null) analytics.setDailyViews(new HashMap<>());
            if (analytics.getDailyTimeSpent() == null) analytics.setDailyTimeSpent(new HashMap<>());
            if (analytics.getDailyViewCounts() == null) analytics.setDailyViewCounts(new HashMap<>());

            analyticsRepository.save(analytics);
        }

        return savedCampaign;
    }
    @Autowired
    private DonationService donationService;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private RewardTierRepository rewardTierRepository;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private StripeService stripeService;

    public List<CrowdFunding> getActiveCampaigns() {
        LocalDate today = LocalDate.now();
        return crowdfundingRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
    }

    public List<CrowdFunding> getCampaignsEndingSoon() {
        LocalDate today = LocalDate.now();
        LocalDate weekFromNow = today.plusDays(7);
        return crowdfundingRepository.findByEndDateBetween(today, weekFromNow);
    }


    public String processRefunds(Long campaignId) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Campaign not found"));

        if (campaign.getCurrentAmount() < campaign.getGoalAmount()) {
            campaign.getDonations().forEach(donation -> {
                try {
                    donationService.refundDonation(donation.getId());
                } catch (StripeException e) {
                    logger.error("Refund failed for donation {}", donation.getId(), e);
                }
            });
            campaign.setStatus("FAILED");
            crowdfundingRepository.save(campaign);
            return "Refunds processed for campaign " + campaignId;
        }
        return "Campaign met its goal. No refunds needed.";
    }

    public Optional<CrowdFunding> getCrowdfundingById(Long id) {
        return crowdfundingRepository.findById(id);
    }

    public CrowdFunding updateCrowdfunding(Long id, @Valid CrowdFunding crowdfundingDetails) {
        CrowdFunding crowdfunding = crowdfundingRepository.findById(id)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Crowdfunding not found with id: " + id));

        // Update basic fields
        crowdfunding.setTitle(crowdfundingDetails.getTitle());
        crowdfunding.setDescription(crowdfundingDetails.getDescription());
        crowdfunding.setGoalAmount(crowdfundingDetails.getGoalAmount());
        crowdfunding.setCurrentAmount(crowdfundingDetails.getCurrentAmount());
        crowdfunding.setChallengeId(crowdfundingDetails.getChallengeId());
        crowdfunding.setCurrency(crowdfundingDetails.getCurrency());
        crowdfunding.setStatus(crowdfundingDetails.getStatus());

        // Update duration fields
        if (crowdfundingDetails.getStartDate() != null) {
            crowdfunding.setStartDate(crowdfundingDetails.getStartDate());
        }
        if (crowdfundingDetails.getEndDate() != null) {
            crowdfunding.setEndDate(crowdfundingDetails.getEndDate());
        }
        if (crowdfundingDetails.getDurationDays() != null) {
            crowdfunding.setDurationDays(crowdfundingDetails.getDurationDays());
        }

        // Recalculate if needed
        if (crowdfunding.getStartDate() != null && crowdfunding.getEndDate() != null) {
            crowdfunding.setDurationDays((int) java.time.temporal.ChronoUnit.DAYS.between(
                    crowdfunding.getStartDate(),
                    crowdfunding.getEndDate()
            ));
        } else if (crowdfunding.getDurationDays() != null && crowdfunding.getStartDate() != null) {
            crowdfunding.setEndDate(crowdfunding.getStartDate().plusDays(crowdfunding.getDurationDays()));
        }

        return crowdfundingRepository.save(crowdfunding);
    }






    public List<CrowdFundingDTO> getAllCampaignsBasic() {
        return crowdfundingRepository.findAll().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<CrowdFundingDTO> getAllCampaignsWithAnalytics() {
        return crowdfundingRepository.findAllWithAnalytics().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
    }
    public CrowdFunding updateCampaignCover(Long id, String imageUrl) {
        CrowdFunding campaign = crowdfundingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));

        campaign.setCoverImageUrl(imageUrl);
        return crowdfundingRepository.save(campaign);
    }

    public CrowdFunding addCampaignMedia(Long id, String mediaUrl) {
        CrowdFunding campaign = crowdfundingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));

        if (campaign.getMediaUrls() == null) {
            campaign.setMediaUrls(new ArrayList<>());
        }
        campaign.getMediaUrls().add(mediaUrl);

        return crowdfundingRepository.save(campaign);
    }


    @Transactional
    public void deleteCrowdfunding(Long id) {
        CrowdFunding crowdfunding = crowdfundingRepository.findById(id)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Crowdfunding not found"));

        // First delete all associated entities

        // 1. Delete analytics if exists
        if (crowdfunding.getAnalytics() != null) {
            analyticsRepository.delete(crowdfunding.getAnalytics());
        }

        // 2. Delete donations
        donationRepository.deleteAll(crowdfunding.getDonations());

        // 3. Delete reward tiers
        rewardTierRepository.deleteAll(crowdfunding.getRewardTiers());

        // 4. Delete milestones
        milestoneRepository.deleteAll(crowdfunding.getMilestones());

        // 5. Delete updates
        updateRepository.deleteAll(crowdfunding.getUpdates());

        // Now delete the campaign itself
        crowdfundingRepository.delete(crowdfunding);
    }

    public CrowdFunding updateCampaignStatus(Long id, String status) {
        CrowdFunding crowdfunding = crowdfundingRepository.findById(id)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Crowdfunding not found"));

        crowdfunding.setStatus(status);
        return crowdfundingRepository.save(crowdfunding);
    }


}