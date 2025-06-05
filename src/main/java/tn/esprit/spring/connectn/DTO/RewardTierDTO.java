package tn.esprit.spring.connectn.DTO;

import lombok.Data;

@Data
public class RewardTierDTO {
    private Long id;
    private Long campaignId;
    private String title;
    private String description;
    private Double minAmount;
    private Integer stock;
    private Integer claimed;
    private String imageUrl;
    private Integer remaining; // Calculated field (stock - claimed)
}