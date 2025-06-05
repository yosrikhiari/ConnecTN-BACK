package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.DTO.IssueDTO.IssueStatisticsDTO;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Entities.IssueStatus;

import java.util.List;

public interface IssueService {
    Issue saveIssue(Issue issue);
    List<Issue> getAllIssues();
    Issue getIssueById(Long id);
    List<Issue> SearchIssuesByTitle(String title);
    Issue updateIssue(Long id, Issue updatedIssue);
    Issue assignUserToIssue(Long issueId, Long userId);
    List<Issue> getIssuesByAssigneeId(Long assigneeId);
    void updateIssueStatus(Long issueId, String status);
    void abandonIssue(Long issueId);
    void updateIssueVerification(Long issueId, boolean isVerified);
    List<Issue> getNonVerifiedIssues();
    void deleteIssueByIdAndReporterId(Long issueId, Long reporterId);

    List<Issue> getIssuesByStatus(IssueStatus status);
    List<Issue> getIssuesByBoundingBox(Double minLat, Double maxLat, Double minLon, Double maxLon);
    void geocodeIssue(Long issueId);
    void batchGeocodeIssues();
    List<IssueStatisticsDTO> getIssueStatisticsByCity();
}