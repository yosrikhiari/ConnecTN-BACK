package tn.esprit.spring.connectn.Repository.CROWDFUNDING;

    import org.springframework.data.jpa.repository.JpaRepository;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignUpdates;

    import java.util.List;


    public interface CampaignUpdateRepository extends JpaRepository<CampaignUpdates, Long> {
        List<CampaignUpdates> findByCampaignIdOrderByPostedAtDesc(Long campaignId);


    }