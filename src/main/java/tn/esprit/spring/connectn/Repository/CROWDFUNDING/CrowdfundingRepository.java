package tn.esprit.spring.connectn.Repository.CROWDFUNDING;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;

import java.time.LocalDate;
import java.util.List;

public interface CrowdfundingRepository extends JpaRepository<CrowdFunding, Long> {
    // Add this query to find active campaigns by date range
    List<CrowdFunding> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);

    // Add this query to find campaigns ending soon
    List<CrowdFunding> findByEndDateBetween(LocalDate start, LocalDate end);

    List<CrowdFunding> findByCreatedBy(Long userId);

    @EntityGraph(attributePaths = {"analytics", "donations"})
    @Query("SELECT c FROM CrowdFunding c LEFT JOIN FETCH c.analytics")
    List<CrowdFunding> findAllWithAnalytics();
}