package tn.esprit.spring.connectn.Repository.CROWDFUNDING;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.RewardTier;

import java.util.List;

public interface
RewardTierRepository extends JpaRepository<RewardTier, Long> {
    List<RewardTier> findByCampaignId(Long campaignId);
}