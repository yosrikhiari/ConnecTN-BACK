package tn.esprit.spring.connectn.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.*;



@Data
public class CrowdFundingDTO {
    private Long id;
    private String title;
    private String description;
    private Double goalAmount;
    private Double currentAmount;
    private String currency;
    private String status;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer durationDays;
    private CampaignAnalyticsDTO analytics;
    private List<RewardTierDTO> rewardTiers;
    private List<MilestoneDTO> milestones;
    private String coverImageUrl;
    private List<String> mediaUrls;


    private Long createdBy;

    private String creatorName; // Added for frontend display
}