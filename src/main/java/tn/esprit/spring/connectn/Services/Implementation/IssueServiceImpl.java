package tn.esprit.spring.connectn.Services.Implementation;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.DTO.IssueDTO.IssueStatisticsDTO;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Entities.IssueStatus;
import tn.esprit.spring.connectn.Repository.CommentRepository;
import tn.esprit.spring.connectn.Repository.IssueRepository;
import tn.esprit.spring.connectn.Repository.ReactionRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IssueService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class IssueServiceImpl implements IssueService {
    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Override
    public Issue saveIssue(Issue issue) {
        issue.setStatus(IssueStatus.OPEN);
        issue.setUpvotes(0);
        issue.setDownvotes(0);
        issue.setCreatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
        issue.setVerified(true);

        // Add geocoding if address is provided but coordinates are not
        if ((issue.getAddress() != null && !issue.getAddress().isEmpty()) &&
                (issue.getLatitude() == null || issue.getLongitude() == null)) {
            geocodingService.geocodeIssue(issue);
        }

        return issueRepository.save(issue);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Issue getIssueById(Long id) {
        Optional<Issue> issue = issueRepository.findById(id);
        if (issue.isPresent()) {
            Issue issue1 = issue.get();
            return issue1;
        } else {
            throw new EntityNotFoundException("Issue not found");
        }
    }


    public List<Issue> SearchIssuesByTitle(String title) {
        return issueRepository.findAllByTitleContaining(title);
    }
    @Override
    public Issue updateIssue(Long id, Issue updatedIssue) {
        Issue existingIssue = issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + id));

        // Update fields
        existingIssue.setTitle(updatedIssue.getTitle());
        existingIssue.setDescription(updatedIssue.getDescription());
        existingIssue.setCategory(updatedIssue.getCategory());
        existingIssue.setStatus(updatedIssue.getStatus());
        existingIssue.setAssigneeId(updatedIssue.getAssigneeId());
        existingIssue.setAddress(updatedIssue.getAddress());
        existingIssue.setLatitude(updatedIssue.getLatitude());
        existingIssue.setLongitude(updatedIssue.getLongitude());
        existingIssue.setCity(updatedIssue.getCity());
        existingIssue.setMediaPaths(updatedIssue.getMediaPaths());

        // Save and return the updated issue
        return issueRepository.save(existingIssue);
    }
    @Override
    public Issue assignUserToIssue(Long issueId, Long userId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        // Set the assigneeId to the user's ID
        issue.setAssigneeId(userId);

        // Change the status to IN_PROGRESS
        issue.setStatus(IssueStatus.IN_PROGRESS);

        // Save and return the updated issue
        return issueRepository.save(issue);
    }
    @Override
    public List<Issue> getIssuesByAssigneeId(Long assigneeId) {
        return issueRepository.findAllByAssigneeId(assigneeId);
    }
    @Override
    public void updateIssueStatus(Long issueId, String status) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        if ("RESOLVED".equals(status)) {
            issue.setStatus(IssueStatus.RESOLVED);
        } else {
            issue.setStatus(IssueStatus.IN_PROGRESS);
        }

        // Save the updated issue
        issueRepository.save(issue);
    }
    @Override
    public void abandonIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        // Set the assigneeId to null
        issue.setAssigneeId(null);

        // Change the status to OPEN
        issue.setStatus(IssueStatus.OPEN);

        // Save the updated issue
        issueRepository.save(issue);
    }
    @Override
    public void updateIssueVerification(Long issueId, boolean isVerified) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found with ID: " + issueId));
        issue.setVerified(isVerified);
        issueRepository.save(issue);
    }

    @Override
    public List<Issue> getNonVerifiedIssues() {
        return issueRepository.findByIsVerifiedFalse();
    }
    @Override
    @Transactional
    public void deleteIssueByIdAndReporterId(Long issueId, Long reporterId) {
        reactionRepository.deleteByIssueId(issueId);
        // Delete dependent rows in issue_comment
        commentRepository.deleteByIssueId(issueId);

        // Delete the issue
        Issue issue = issueRepository.findByIdAndReporterId(issueId, reporterId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with ID: " + issueId + " and Reporter ID: " + reporterId));
        issueRepository.delete(issue);
    }
    @Override
    public List<Issue> getIssuesByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status);
    }

    @Override
    public List<Issue> getIssuesByBoundingBox(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        return issueRepository.findByLatitudeBetweenAndLongitudeBetween(minLat, maxLat, minLon, maxLon);
    }

    @Override
    @Transactional
    public void geocodeIssue(Long issueId) {
        issueRepository.findById(issueId).ifPresent(geocodingService::geocodeIssue);
    }

    @Override
    @Transactional
    public void batchGeocodeIssues() {
        List<Issue> issues = issueRepository.findByLatitudeIsNullAndAddressIsNotNull();
        log.info("Starting batch geocoding for {} issues", issues.size());

        issues.forEach(issue -> {
            try {
                geocodingService.geocodeIssue(issue);
                log.debug("Geocoded issue ID: {}", issue.getId());
            } catch (Exception e) {
                log.error("Failed to geocode issue ID {}: {}", issue.getId(), e.getMessage());
            }
        });

        issueRepository.saveAll(issues);
        log.info("Completed batch geocoding");
    }
    @Override
    public List<IssueStatisticsDTO> getIssueStatisticsByCity() {
        List<Issue> allIssues = issueRepository.findAll();

        Map<String, IssueStatisticsDTO> statisticsByCity = new HashMap<>();

        for (Issue issue : allIssues) {
            String city = issue.getCity();
            if (city == null || city.isEmpty()) {
                continue;
            }

            IssueStatisticsDTO cityStats = statisticsByCity.getOrDefault(city, new IssueStatisticsDTO());
            cityStats.setCity(city);

            switch (issue.getStatus()) {
                case OPEN:
                    cityStats.setOpenCount(cityStats.getOpenCount() + 1);
                    break;
                case IN_PROGRESS:
                    cityStats.setInProgressCount(cityStats.getInProgressCount() + 1);
                    break;
                case RESOLVED:
                    cityStats.setResolvedCount(cityStats.getResolvedCount() + 1);
                    break;
            }

            statisticsByCity.put(city, cityStats);
        }

        return new ArrayList<>(statisticsByCity.values());
    }
}
