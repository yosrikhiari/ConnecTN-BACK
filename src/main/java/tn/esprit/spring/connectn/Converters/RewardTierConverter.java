package tn.esprit.spring.connectn.Converters;



import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.RewardTierDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.RewardTier;

@Component
public class RewardTierConverter {

    public RewardTierDTO toDto(RewardTier entity) {
        RewardTierDTO dto = new RewardTierDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaign().getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setMinAmount(entity.getMinAmount());
        dto.setStock(entity.getStock());
        dto.setClaimed(entity.getClaimed());
        dto.setRemaining(entity.getStock() - entity.getClaimed());
        return dto;
    }

    public RewardTier toEntity(RewardTierDTO dto) {
        RewardTier entity = new RewardTier();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setMinAmount(dto.getMinAmount());
        entity.setStock(dto.getStock());
        entity.setClaimed(dto.getClaimed());
        return entity;
    }
}