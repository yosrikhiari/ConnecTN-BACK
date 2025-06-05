package tn.esprit.spring.connectn.Converters;



import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignAnalyticsDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignAnalytics;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class CampaignAnalyticsConverter {

    public CampaignAnalyticsDTO toDto(CampaignAnalytics entity) {
        CampaignAnalyticsDTO dto = new CampaignAnalyticsDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaign().getId());
        dto.setPageViews(entity.getPageViews());
        dto.setUniqueVisitors(entity.getUniqueVisitors());
        dto.setShares(entity.getShares());

        // Calculate conversion rate if needed
        if (entity.getPageViews() > 0 && entity.getCampaign() != null) {
            double donors = entity.getCampaign().getDonations().size();
            dto.setConversionRate((donors / entity.getPageViews()) * 100);
        }

        Map<String, Double> avgTimeSpent = new HashMap<>();
        entity.getDailyTimeSpent().forEach((date, totalSeconds) -> {
            Integer views = entity.getDailyViewCounts().getOrDefault(date, 1);
            avgTimeSpent.put(date.toString(), (double) totalSeconds / views);
        });
        dto.setAvgTimeSpent(avgTimeSpent);
        dto.setDonationsCount(entity.getDonationsCount());
        dto.setAverageDonationAmount(entity.getAverageDonationAmount());
        dto.setReturningDonors(entity.getReturningDonors());
        dto.setRewardClaims(entity.getRewardClaims());
        dto.setRefundedDonations(entity.getRefundedDonations());
        dto.setTotalRefundedAmount(entity.getTotalRefundedAmount() != null ? entity.getTotalRefundedAmount() : 0.0);
        return dto;
    }

    public CampaignAnalytics toEntity(CampaignAnalyticsDTO dto) {
        CampaignAnalytics entity = new CampaignAnalytics();
        entity.setId(dto.getId());
        entity.setPageViews(dto.getPageViews());
        entity.setUniqueVisitors(dto.getUniqueVisitors());
        entity.setShares(dto.getShares());
        entity.setDonationsCount(dto.getDonationsCount());
        entity.setAverageDonationAmount(dto.getAverageDonationAmount());
        entity.setReturningDonors(dto.getReturningDonors());
        entity.setRewardClaims(dto.getRewardClaims());
        entity.setRefundedDonations(dto.getRefundedDonations());
        entity.setTotalRefundedAmount(dto.getTotalRefundedAmount());

        // Convert String dates to LocalDate for maps
        if (dto.getDailyViews() != null) {
            Map<LocalDate, Integer> dailyViews = new HashMap<>();
            dto.getDailyViews().forEach((dateStr, count) ->
                    dailyViews.put(LocalDate.parse(dateStr), count));
            entity.setDailyViews(dailyViews);
        }

        if (dto.getAvgTimeSpent() != null) {
            Map<LocalDate, Long> timeSpent = new HashMap<>();
            dto.getAvgTimeSpent().forEach((dateStr, minutes) ->
                    timeSpent.put(LocalDate.parse(dateStr), (long)(minutes * 60)));
            entity.setDailyTimeSpent(timeSpent);
        }

        return entity;
    }
}