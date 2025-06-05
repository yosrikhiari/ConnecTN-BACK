package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignAnalyticsDTO;
import tn.esprit.spring.connectn.Services.CROWDFUNDING.CampaignAnalyticsService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/analytics")
public class CampaignAnalyticsController {

    @Autowired
    private CampaignAnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<CampaignAnalyticsDTO> getAnalytics(@PathVariable Long campaignId) {
        return ResponseEntity.ok(analyticsService.getCampaignAnalyticsDto(campaignId));
    }

    @PostMapping("/donation")
    public ResponseEntity<Void> recordDonation(
            @PathVariable Long campaignId,
            @RequestParam Double amount,
            @RequestParam(required = false) Long userId) {
        analyticsService.recordDonation(campaignId, amount, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reward-claim")
    public ResponseEntity<Void> recordRewardClaim(@PathVariable Long campaignId) {
        analyticsService.recordRewardClaim(campaignId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/timeline")
    public ResponseEntity<Map<String, Object>> getTimelineData(@PathVariable Long campaignId) {
        Map<String, Object> response = new HashMap<>();

        // Convert LocalDate to String for JSON serialization
        Map<String, Integer> views = analyticsService.getDailyViews(campaignId).entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));

        Map<String, Double> donations = analyticsService.getDailyDonations(campaignId).entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));

        response.put("views", views);
        response.put("donations", donations);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pageview")
    public ResponseEntity<Void> recordPageView(@PathVariable Long campaignId) {
        analyticsService.recordPageView(campaignId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/visit")
    public ResponseEntity<Void> recordUniqueVisitor(
            @PathVariable Long campaignId,
            @RequestParam Long userId) {
        analyticsService.recordUniqueVisitor(campaignId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/share")
    public ResponseEntity<Void> recordShare(@PathVariable Long campaignId) {
        analyticsService.recordShare(campaignId);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/time-spent")
    public ResponseEntity<Void> recordTimeSpent(
            @PathVariable Long campaignId,
            @RequestParam Integer seconds
    ) {
        analyticsService.recordTimeSpent(campaignId, seconds);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/daily-stats")
    public ResponseEntity<Map<String, Object>> getDailyStats(@PathVariable Long campaignId) {
        Map<String, Object> response = new HashMap<>();

        // Convert LocalDate to String keys for the response
        Map<String, Integer> dailyViews = analyticsService.getDailyViews(campaignId).entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));

        Map<String, Double> avgTimeSpent = analyticsService.getDailyTimeSpent(campaignId).entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue
                ));

        response.put("dailyViews", dailyViews);
        response.put("avgTimeSpent", avgTimeSpent);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/refund")
    public ResponseEntity<Void> recordRefund(
            @PathVariable Long campaignId,
            @RequestParam Double amount,
            @RequestParam(required = false) Long userId) {
        analyticsService.recordRefund(campaignId, amount, userId);
        return ResponseEntity.ok().build();
    }
}