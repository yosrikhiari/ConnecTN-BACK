package tn.esprit.spring.connectn.Repository.CROWDFUNDING;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignComment;

import java.util.List;



public interface CampaignCommentRepository extends JpaRepository<CampaignComment, Long> {
    List<CampaignComment> findByCampaignIdAndParentIsNullOrderByTimestampDesc(Long campaignId);
    List<CampaignComment> findByParentIdOrderByTimestampAsc(Long parentId);
    List<CampaignComment> findByParentId(Long parentId);


    List<CampaignComment> findByUserId(Long userId);
}