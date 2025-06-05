package tn.esprit.spring.connectn.Repository.CROWDFUNDING;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignAnalytics;

import java.util.Optional;

@Repository
public interface CampaignAnalyticsRepository extends JpaRepository<CampaignAnalytics, Long> {
    Optional<CampaignAnalytics> findByCampaignId(Long campaignId);
}