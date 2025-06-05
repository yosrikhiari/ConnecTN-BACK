package tn.esprit.spring.connectn.Converters;

import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignUpdateDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignUpdates;

@Component
public class CampaignUpdateConverter {

    public CampaignUpdateDTO toDto(CampaignUpdates entity, Long currentUserId) {
        if (entity == null) {
            return null;
        }

        CampaignUpdateDTO dto = new CampaignUpdateDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null);
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setPostedAt(entity.getPostedAt());
        dto.setViewCount(entity.getViewCount() != null ? entity.getViewCount() : 0);
        dto.setIsMilestone(entity.getIsMilestone() != null ? entity.getIsMilestone() : false);
        dto.setMediaUrls(entity.getMediaUrls());
        dto.setLikes(entity.getLikes() != null ? entity.getLikes() : 0);
        dto.setShares(entity.getShares() != null ? entity.getShares() : 0);
        dto.setLikedByUsers(entity.getLikedByUsers());
        dto.setBookmarkedByUsers(entity.getBookmarkedByUsers());

        if (currentUserId != null && entity.getLikedByUsers() != null) {
            dto.setUserLiked(entity.getLikedByUsers().contains(currentUserId));
        }
        if (currentUserId != null && entity.getBookmarkedByUsers() != null) {
            dto.setUserBookmarked(entity.getBookmarkedByUsers().contains(currentUserId));
        }

        return dto;
    }

    public CampaignUpdates toEntity(CampaignUpdateDTO dto) {
        if (dto == null) {
            return null;
        }

        CampaignUpdates entity = new CampaignUpdates();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPostedAt(dto.getPostedAt());
        entity.setViewCount(dto.getViewCount() != null ? dto.getViewCount() : 0);
        entity.setIsMilestone(dto.getIsMilestone() != null ? dto.getIsMilestone() : false);
        entity.setMediaUrls(dto.getMediaUrls());
        entity.setLikes(dto.getLikes() != null ? dto.getLikes() : 0);
        entity.setShares(dto.getShares() != null ? dto.getShares() : 0);
        entity.setLikedByUsers(dto.getLikedByUsers());
        entity.setBookmarkedByUsers(dto.getBookmarkedByUsers());

        return entity;
    }
}