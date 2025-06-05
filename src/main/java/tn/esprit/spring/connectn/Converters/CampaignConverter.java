package tn.esprit.spring.connectn.Converters;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CrowdFundingDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;

import java.util.stream.Collectors;

@Component
public class CampaignConverter {
    @Autowired
    private RewardTierConverter rewardTierConverter;
    @Autowired
    private MilestoneConverter milestoneConverter;
    @Autowired
    private CampaignAnalyticsConverter analyticsConverter;

    public CrowdFundingDTO toDto(CrowdFunding entity) {
        if (entity == null) return null;

        CrowdFundingDTO dto = new CrowdFundingDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setGoalAmount(entity.getGoalAmount());
        dto.setCurrentAmount(entity.getCurrentAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        // Directly use LocalDate fields
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setDurationDays(entity.getDurationDays());

        // Set cover image and media URLs
        dto.setCoverImageUrl(entity.getCoverImageUrl());
        dto.setMediaUrls(entity.getMediaUrls());

        // Convert nested objects
        if (entity.getAnalytics() != null) {
            dto.setAnalytics(analyticsConverter.toDto(entity.getAnalytics()));
        }

        if (entity.getRewardTiers() != null) {
            dto.setRewardTiers(entity.getRewardTiers().stream()
                    .map(rewardTierConverter::toDto)
                    .collect(Collectors.toList()));
        }

        if (entity.getMilestones() != null) {
            dto.setMilestones(entity.getMilestones().stream()
                    .map(milestoneConverter::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public CrowdFunding toEntity(CrowdFundingDTO dto) {
        if (dto == null) return null;

        CrowdFunding entity = new CrowdFunding();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setGoalAmount(dto.getGoalAmount());
        entity.setCurrentAmount(dto.getCurrentAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setCreatedBy(dto.getCreatedBy());


        // Directly use LocalDate fields
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        // Set cover image and media URLs
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setMediaUrls(dto.getMediaUrls());

        return entity;
    }
}