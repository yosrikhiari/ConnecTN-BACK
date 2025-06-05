package tn.esprit.spring.connectn.Services.CROWDFUNDING;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.CampaignUpdateConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignUpdateDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignUpdates;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Donation;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Notification;
import tn.esprit.spring.connectn.Exceptions.CROWDFUNDING.CrowdfundingNotFoundException;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CampaignUpdateRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.DonationRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(CampaignUpdateService.class);
    private final CampaignUpdateRepository updateRepository;
    private final CrowdfundingRepository crowdfundingRepository;
    private final DonationRepository donationRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final CampaignUpdateConverter updateConverter;

    @Transactional
    public CampaignUpdates createEngagingUpdate(Long campaignId, CampaignUpdates update) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Campaign not found"));

        // Set core relationships
        update.setCampaign(campaign);
        update.setPostedAt(LocalDateTime.now());

        // Auto-generate engagement content if needed
        if (update.getContent() == null || update.getContent().isBlank()) {
            update.setContent(generateUpdateContent(campaign));
        }

        CampaignUpdates savedUpdate = updateRepository.save(update);

        // Notify all donors (transparency driver)
        notifyCampaignDonors(campaignId, savedUpdate);

        return savedUpdate;
    }
    private void notifyCampaignDonors(Long campaignId, CampaignUpdates update) {
        // 1. Get all donors for this campaign
        List<Donation> donations = donationRepository.findByCrowdfundingId(campaignId);

        // 2. Create a notification template
        String notificationTitle = "New Update: " + update.getTitle();
        String notificationMessage = String.format(
                "The campaign you supported has a new update: %s...",
                update.getContent().length() > 50
                        ? update.getContent().substring(0, 50) + "..."
                        : update.getContent()
        );

        // 3. Create notifications for each donor
        donations.forEach(donation -> {
            Notification notification = new Notification();
            notification.setUserId(donation.getUserId());
            notification.setTitle(notificationTitle);
            notification.setMessage(notificationMessage);
            notification.setType("CAMPAIGN_UPDATE");
            notification.setReferenceId(campaignId);
            notification.setActionUrl("/campaigns/" + campaignId + "/updates");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        });

        // Optional: Log how many notifications were sent
        logger.info("Sent {} notifications for campaign update {}", donations.size(), update.getId());
    }





    @Transactional
    public void trackView(Long updateId) {
        updateRepository.findById(updateId).ifPresent(update -> {
            update.setViewCount((update.getViewCount() != null ? update.getViewCount() : 0) + 1);
            updateRepository.save(update);
        });
    }


    public Map<String, Object> getEngagementStatus(Long updateId, Long userId) {
        CampaignUpdates update = updateRepository.findById(updateId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Update not found"));

        return Map.of(
                "liked", update.getLikedByUsers().contains(userId),
                "bookmarked", update.getBookmarkedByUsers().contains(userId),
                "likeCount", update.getLikes()
        );
    }



    private String generateUpdateContent(CrowdFunding campaign) {
        // Dynamic content generation to encourage engagement
        double progress = (campaign.getCurrentAmount() / campaign.getGoalAmount()) * 100;

        return String.format(
                "Our campaign is %.0f%% funded! We've raised %s %s of our %s %s goal. " +
                        "Your support is making this possible. Here's what we've accomplished recently...",
                progress,
                campaign.getCurrentAmount(),
                campaign.getCurrency(),
                campaign.getGoalAmount(),
                campaign.getCurrency()
        );
    }

    private void notifyDonorsAboutUpdate(CrowdFunding campaign, CampaignUpdates update) {
        // Get all donors for this campaign
        List<Donation> donations = donationRepository.findByCrowdfundingId(campaign.getId());

        donations.forEach(donation -> {
            notificationService.createNotification(
                    donation.getUserId(),
                    "New Update: " + update.getTitle(),
                    "The campaign '" + campaign.getTitle() + "' has a new update",
                    "CAMPAIGN_UPDATE",
                    campaign.getId(),
                    "/campaigns/" + campaign.getId() + "/updates"
            );
        });
    }

    @Transactional(readOnly = true)
    public List<CampaignUpdateDTO> getUpdates(Long campaignId, Long userId) {
        List<CampaignUpdates> updates = updateRepository.findByCampaignIdOrderByPostedAtDesc(campaignId);
        return updates.stream()
                .map(update -> updateConverter.toDto(update, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CampaignUpdateDTO createUpdate(Long campaignId, CampaignUpdateDTO updateDTO) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Campaign not found"));
        CampaignUpdates update = updateConverter.toEntity(updateDTO);
        update.setCampaign(campaign);
        update.setPostedAt(LocalDateTime.now());
        // Auto-generate engagement content if needed
        if (update.getContent() == null || update.getContent().isBlank()) {
            update.setContent(generateUpdateContent(campaign));
        }
        CampaignUpdates savedUpdate = updateRepository.save(update);
        notifyDonorsAboutUpdate(campaign, savedUpdate);
        return updateConverter.toDto(savedUpdate, null); // Pass currentUserId if available
    }

    @Transactional
    public CampaignUpdateDTO updateUpdate(Long updateId, CampaignUpdateDTO updateDTO) {
        CampaignUpdates existingUpdate = updateRepository.findById(updateId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Update not found"));

        existingUpdate.setTitle(updateDTO.getTitle());
        existingUpdate.setContent(updateDTO.getContent());
        // Update other fields as needed

        CampaignUpdates updatedUpdate = updateRepository.save(existingUpdate);
        return updateConverter.toDto(updatedUpdate, null); // Pass currentUserId if available
    }

    @Transactional
    public void toggleLike(Long updateId, Long userId) {
        CampaignUpdates update = updateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Update not found"));

        Set<Long> likedByUsers = update.getLikedByUsers();
        if (likedByUsers.contains(userId)) {
            likedByUsers.remove(userId);
            update.setLikes(Math.max(0, update.getLikes() - 1));
        } else {
            likedByUsers.add(userId);
            update.setLikes(update.getLikes() + 1);
        }
        updateRepository.save(update);
    }

    @Transactional
    public void toggleBookmark(Long updateId, Long userId) {
        CampaignUpdates update = updateRepository.findById(updateId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Update not found"));

        if (update.getBookmarkedByUsers().contains(userId)) {
            update.getBookmarkedByUsers().remove(userId);
        } else {
            update.getBookmarkedByUsers().add(userId);
        }
        updateRepository.save(update);
    }

    @Transactional
    public void deleteUpdate(Long updateId) {
        updateRepository.deleteById(updateId);
    }

    public CampaignUpdates getUpdateById(Long updateId) {
        return updateRepository.findById(updateId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Update not found with id: " + updateId));
    }
}