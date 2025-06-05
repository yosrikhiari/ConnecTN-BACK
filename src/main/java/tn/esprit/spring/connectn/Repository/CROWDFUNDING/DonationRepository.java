package tn.esprit.spring.connectn.Repository.CROWDFUNDING;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.Donation;

import java.time.LocalDate;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    // Keep your other methods

    List<Donation> findByUserId(Long userId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.userId = :userId")
    Double sumAmountByUserId(@Param("userId") Long userId);

    List<Donation> findByCrowdfundingId(Long crowdfundingId);

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.crowdfunding LEFT JOIN FETCH d.rewardTier")
    List<Donation> findAllWithRelationships();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.crowdfunding.id = :campaignId AND DATE(d.timestamp) = :date")
    Double getTotalDonationsByCampaignAndDate(@Param("campaignId") Long campaignId, @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Donation d WHERE d.crowdfunding.id = :crowdfundingId AND d.userId = :userId")
    boolean existsByCampaignAndUser(@Param("crowdfundingId") Long crowdfundingId,
                                    @Param("userId") Long userId);


    @Query("SELECT COUNT(DISTINCT d.userId) FROM Donation d WHERE d.crowdfunding.id = :campaignId")
    int countDistinctUserIdByCampaignId(@Param("campaignId") Long campaignId);


    @Query("SELECT COUNT(DISTINCT d.userId), COUNT(d.id), COALESCE(SUM(d.amount),0) " +
            "FROM Donation d WHERE d.crowdfunding.id = :campaignId")
    Object[] getBackerStats(@Param("campaignId") Long campaignId);


}