package tn.esprit.spring.connectn.Services.CROWDFUNDING;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.MilestoneConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.MilestoneDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Milestones;
import tn.esprit.spring.connectn.Exceptions.CROWDFUNDING.CrowdfundingNotFoundException;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.MilestoneRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final CrowdfundingRepository crowdfundingRepository;
    private final NotificationService notificationService;
    private final MilestoneConverter milestoneConverter;

    @Transactional
    public MilestoneDTO createMilestone(Long campaignId, MilestoneDTO milestoneDTO) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Campaign not found"));

        Milestones milestone = milestoneConverter.toEntity(milestoneDTO);
        milestone.setCampaign(campaign);
        milestone.setIsReached(false);

        Milestones saved = milestoneRepository.save(milestone);
        return milestoneConverter.toDto(saved);
    }

    public List<MilestoneDTO> getCampaignMilestones(Long campaignId) {
        return milestoneRepository.findByCampaignIdOrderByTargetAmount(campaignId).stream()
                .map(milestoneConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMilestone(Long milestoneId) {
        milestoneRepository.deleteById(milestoneId);
    }

    private void triggerMilestoneNotification(CrowdFunding campaign, Milestones milestone) {
        // Only if notification service is available
        if (notificationService != null) {
            String message = String.format(
                    "Campaign '%s' reached milestone: %s (%.0f%% funded)",
                    campaign.getTitle(),
                    milestone.getTitle(),
                    (campaign.getCurrentAmount() / campaign.getGoalAmount()) * 100
            );

            notificationService.createNotification(
                    campaign.getCreatedBy(), // Assuming creator wants notifications
                    "Milestone Reached!",
                    message,
                    "MILESTONE",
                    campaign.getId(),
                    "/campaigns/" + campaign.getId()
            );
        }
    }
}