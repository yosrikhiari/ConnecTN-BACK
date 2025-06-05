package tn.esprit.spring.connectn.DTO;

import lombok.Data;

@Data
public class MilestoneDTO {
    private Long id;
    private Long campaignId;
    private String title;
    private Double targetAmount;
    private Boolean isReached;
    private Double progressPercentage; // Calculated field
}