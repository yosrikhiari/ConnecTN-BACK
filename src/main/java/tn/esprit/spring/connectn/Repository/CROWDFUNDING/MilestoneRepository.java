package tn.esprit.spring.connectn.Repository.CROWDFUNDING;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Milestones;

    import java.util.List;

    public interface MilestoneRepository extends JpaRepository<Milestones, Long> {
        List<Milestones> findByCampaignIdOrderByTargetAmount(Long campaignId);

        @Query("SELECT m FROM Milestones m WHERE m.campaign.id = :campaignId AND m.isReached = false")
        List<Milestones> findByCampaignIdAndIsReachedFalse(@Param("campaignId") Long campaignId);
    }