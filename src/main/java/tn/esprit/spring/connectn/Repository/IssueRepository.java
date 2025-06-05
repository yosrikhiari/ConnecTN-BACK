package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Entities.IssueStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByTitleContaining(String title);
    List<Issue> findAllByAssigneeId(Long assigneeId);
    List<Issue> findByIsVerifiedFalse();
    Optional<Issue> findByIdAndReporterId(Long issueId, Long reporterId);
    List<Issue> findByLatitudeIsNullAndAddressIsNotNull();

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByLatitudeBetweenAndLongitudeBetween(Double minLat, Double maxLat, Double minLon, Double maxLon);
}
