package tn.esprit.spring.connectn.Converters;


import org.springframework.stereotype.Component;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.DonationDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Donation;

import java.time.LocalDateTime;

@Component
public class DonationConverter {
    public DonationDTO toDto(Donation entity) {
        DonationDTO dto = new DonationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setTimestamp(entity.getTimestamp());
        dto.setPaymentIntentId(entity.getPaymentIntentId());
        dto.setAnonymous(entity.isAnonymous());
        dto.setMessage(entity.getMessage());

        if (entity.getCrowdfunding() != null) {
            dto.setCampaignId(entity.getCrowdfunding().getId());
        }


        if (entity.getRewardTier() != null) {
            dto.setRewardTierId(entity.getRewardTier().getId());
        }


        return dto;
    }

    public Donation toEntity(DonationDTO dto) {
        Donation entity = new Donation();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setAmount(dto.getAmount());
        entity.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");
        entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        entity.setPaymentIntentId(dto.getPaymentIntentId());
        entity.setAnonymous(dto.isAnonymous());
        entity.setMessage(dto.getMessage());

        return entity;
    }
}