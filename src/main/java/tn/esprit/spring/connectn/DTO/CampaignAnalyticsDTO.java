package tn.esprit.spring.connectn.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class CampaignAnalyticsDTO {

    private Long id;
    private Long campaignId;
    private Integer pageViews;
    private Integer uniqueVisitors;
    private Integer shares;
    private Double conversionRate;

    private Integer donationsCount;
    private Double averageDonationAmount;
    private Integer returningDonors;
    private Integer rewardClaims;

    private Map<String, Integer> dailyViews;
    private Map<String, Double> goalProgress;
    private Map<String, Double> avgTimeSpent;

    private Integer refundedDonations;
    private Double totalRefundedAmount = 0.0;
}