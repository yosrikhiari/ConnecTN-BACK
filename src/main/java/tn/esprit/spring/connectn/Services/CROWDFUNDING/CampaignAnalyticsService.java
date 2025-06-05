package tn.esprit.spring.connectn.Services.CROWDFUNDING;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.CampaignAnalyticsConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignAnalyticsDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignAnalytics;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CampaignAnalyticsRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.DonationRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CampaignAnalyticsService {

    @Autowired
    private CampaignAnalyticsConverter analyticsConverter;

    @Autowired
    private CampaignAnalyticsRepository analyticsRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CrowdfundingRepository crowdfundingRepository;


    public CampaignAnalyticsDTO getCampaignAnalyticsDto(Long campaignId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);
        CampaignAnalyticsDTO dto = analyticsConverter.toDto(analytics);

        // Convert LocalDate keys to String for frontend
        Map<String, Integer> dailyViews = analytics.getDailyViews().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
        dto.setDailyViews(dailyViews);

        // Convert avgTimeSpent
        Map<String, Double> avgTimeSpent = getDailyTimeSpent(campaignId).entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));
        dto.setAvgTimeSpent(avgTimeSpent);

        return dto;
    }

    private static final Logger logger = LoggerFactory.getLogger(CampaignAnalyticsService.class);

    @Transactional
    public void recordDonation(Long campaignId, Double amount, Long userId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);

        // Always increment total donations count
        analytics.setDonationsCount(analytics.getDonationsCount() + 1);

        // Update average donation amount
        double totalDonations = analytics.getAverageDonationAmount() * (analytics.getDonationsCount() - 1);
        analytics.setAverageDonationAmount((totalDonations + amount) / analytics.getDonationsCount());

        // Only increment returning donors if this is a repeat donation
        if (userId != null && donationRepository.existsByCampaignAndUser(campaignId, userId)) {
            analytics.setReturningDonors(analytics.getReturningDonors() + 1);
        }

        analyticsRepository.save(analytics);
    }


    @Transactional
    public void recordRefund(Long campaignId, Double amount, Long userId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);

        // Decrease donations count (as before)
        analytics.setDonationsCount(Math.max(0, analytics.getDonationsCount() - 1));

        // Update average donation amount (as before)
        if (analytics.getDonationsCount() > 0) {
            double totalDonations = analytics.getAverageDonationAmount() * (analytics.getDonationsCount() + 1);
            analytics.setAverageDonationAmount((totalDonations - amount) / analytics.getDonationsCount());
        } else {
            analytics.setAverageDonationAmount(0.0);
        }
        // Increment refunded donations count
        // Increment refund counters
        analytics.setRefundedDonations(analytics.getRefundedDonations() + 1);
        analytics.setTotalRefundedAmount(
                (analytics.getTotalRefundedAmount() != null ? analytics.getTotalRefundedAmount() : 0.0) + amount
        );

        analyticsRepository.save(analytics);
    }


    public Map<LocalDate, Integer> getDailyViews(Long campaignId) {
        CampaignAnalytics analytics = analyticsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Analytics not found"));
        return analytics.getDailyViews();
    }

    public Map<LocalDate, Double> getDailyDonations(Long campaignId) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Double> dailyDonations = new LinkedHashMap<>();

        // Get donations for last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Double amount = donationRepository.getTotalDonationsByCampaignAndDate(campaignId, date);
            dailyDonations.put(date, amount != null ? amount : 0.0);
        }

        return dailyDonations;
    }

    @Transactional
    public void recordRewardClaim(Long campaignId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);
        analytics.setRewardClaims(analytics.getRewardClaims() + 1);
        analyticsRepository.save(analytics);
    }


    @Transactional
    public void recordPageView(Long campaignId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);
        analytics.setPageViews(analytics.getPageViews() + 1);
        LocalDate today = LocalDate.now();
        analytics.getDailyViews().merge(today, 1, Integer::sum);

        analyticsRepository.save(analytics);
    }

    @Transactional
    public void recordUniqueVisitor(Long campaignId, Long userId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);
        analytics.setUniqueVisitors(analytics.getUniqueVisitors() + 1);
        analyticsRepository.save(analytics);
    }

    @Transactional
    public void recordShare(Long campaignId) {
        CampaignAnalytics analytics = getOrCreateAnalytics(campaignId);
        analytics.setShares(analytics.getShares() + 1);
        analyticsRepository.save(analytics);
    }



    private CampaignAnalytics getOrCreateAnalytics(Long campaignId) {
        return analyticsRepository.findByCampaignId(campaignId)
                .orElseGet(() -> {
                    CrowdFunding campaign = crowdfundingRepository.findById(campaignId).orElseThrow();
                    CampaignAnalytics newAnalytics = new CampaignAnalytics();
                    newAnalytics.setCampaign(campaign);
                    return analyticsRepository.save(newAnalytics);
                });
    }


    @Transactional
    public void recordTimeSpent(Long campaignId, long seconds) {
        try {
            // First check if campaign exists
            if (!crowdfundingRepository.existsById(campaignId)) {
                throw new EntityNotFoundException("Campaign not found with id: " + campaignId);
            }

            CampaignAnalytics analytics = analyticsRepository.findByCampaignId(campaignId)
                    .orElseGet(() -> {
                        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
                        CampaignAnalytics newAnalytics = new CampaignAnalytics();
                        newAnalytics.setCampaign(campaign);
                        return analyticsRepository.save(newAnalytics);
                    });

            LocalDate today = LocalDate.now();

            // Update total time spent
            analytics.getDailyTimeSpent().merge(today, seconds, Long::sum);

            // Increment view count for average calculation
            analytics.getDailyViewCounts().merge(today, 1, Integer::sum);

            analyticsRepository.save(analytics);
        } catch (Exception e) {
            // Log the error
            logger.error("Error recording time spent for campaign " + campaignId, e);
            throw e; // Re-throw to return 500 error
        }
    }


    // In CampaignAnalyticsService
    public Map<LocalDate, Double> getDailyTimeSpent(Long campaignId) {
        CampaignAnalytics analytics = analyticsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Analytics not found"));

        Map<LocalDate, Double> result = new HashMap<>();

        // Calculate average time spent per day
        analytics.getDailyTimeSpent().forEach((date, totalSeconds) -> {
            Integer views = analytics.getDailyViewCounts().getOrDefault(date, 1);
            result.put(date, (double) totalSeconds / views);
        });

        return result;
    }
}
