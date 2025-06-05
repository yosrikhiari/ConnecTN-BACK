package tn.esprit.spring.connectn.Converters;


import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignCommentDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignComment;

@Component
public class CampaignCommentConverter {

    public CampaignCommentDTO toDto(CampaignComment entity) {
        CampaignCommentDTO dto = new CampaignCommentDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaign().getId());
        dto.setUserId(entity.getUserId());
        dto.setText(entity.getText());
        dto.setTimestamp(entity.getTimestamp());
        dto.setLikes(entity.getLikes());
        dto.setUsername("User " + entity.getUserId());

        // Safely handle userVotes
        if (entity.getUserVotes() != null) {
            dto.getUserVotes().putAll(entity.getUserVotes());
        }

        if (entity.getParent() != null) {
            dto.setParentId(entity.getParent().getId());
        }

        return dto;
    }

    public CampaignComment toEntity(CampaignCommentDTO dto) {
        CampaignComment entity = new CampaignComment();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setText(dto.getText());
        entity.setTimestamp(dto.getTimestamp());
        entity.setLikes(dto.getLikes());

        // Note: Campaign and Parent relationships should be set separately
        return entity;
    }
}