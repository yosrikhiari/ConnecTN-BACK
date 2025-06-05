package tn.esprit.spring.connectn.Services.CROWDFUNDING;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.RewardTierConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.RewardTierDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.RewardTier;
import tn.esprit.spring.connectn.Exceptions.CROWDFUNDING.CrowdfundingNotFoundException;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.RewardTierRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardTierService {
    private final RewardTierRepository repository;
    private final CrowdfundingRepository crowdfundingRepository;
    private final RewardTierConverter rewardTierConverter;

    @Transactional
    public RewardTierDTO createReward(Long campaignId, RewardTierDTO rewardDTO) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId)
                .orElseThrow(() -> new CrowdfundingNotFoundException("Campaign not found"));

        RewardTier reward = rewardTierConverter.toEntity(rewardDTO);
        reward.setCampaign(campaign);
        return rewardTierConverter.toDto(repository.save(reward));
    }

    public List<RewardTierDTO> getCampaignRewards(Long campaignId) {
        return repository.findByCampaignId(campaignId).stream()
                .map(rewardTierConverter::toDto)
                .collect(Collectors.toList());
    }
}