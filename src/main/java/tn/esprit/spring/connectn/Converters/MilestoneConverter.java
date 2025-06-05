package tn.esprit.spring.connectn.Converters;



import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.MilestoneDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Milestones;

@Component
public class MilestoneConverter {

    public MilestoneDTO toDto(Milestones entity) {
        MilestoneDTO dto = new MilestoneDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaign().getId());
        dto.setTitle(entity.getTitle());
        dto.setTargetAmount(entity.getTargetAmount());
        dto.setIsReached(entity.getIsReached());

        // Calculate progress percentage if campaign is available
        if (entity.getCampaign() != null && entity.getTargetAmount() > 0) {
            double progress = (entity.getCampaign().getCurrentAmount() / entity.getTargetAmount()) * 100;
            dto.setProgressPercentage(Math.min(progress, 100)); // Cap at 100%
        }

        return dto;
    }

    public Milestones toEntity(MilestoneDTO dto) {
        Milestones entity = new Milestones();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setTargetAmount(dto.getTargetAmount());
        entity.setIsReached(dto.getIsReached());
        return entity;
    }
}