package tn.esprit.spring.connectn.Services.CROWDFUNDING;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Converters.CampaignCommentConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignCommentDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignComment;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CampaignCommentRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignCommentService {

    @Autowired
    private CampaignCommentRepository commentRepository;

    @Autowired
    private CrowdfundingRepository crowdfundingRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CampaignCommentConverter commentConverter;

    public List<CampaignCommentDTO> getCommentsByCampaign(Long campaignId) {
        // Fetch only top-level comments (where parent is null)
        List<CampaignComment> topLevelComments = commentRepository.findByCampaignIdAndParentIsNullOrderByTimestampDesc(campaignId);

        return topLevelComments.stream()
                .map(this::convertCommentWithReplies)
                .collect(Collectors.toList());
    }

    private CampaignCommentDTO convertCommentWithReplies(CampaignComment comment) {
        CampaignCommentDTO dto = commentConverter.toDto(comment);

        // Recursively fetch and convert replies
        List<CampaignComment> replies = commentRepository.findByParentIdOrderByTimestampAsc(comment.getId());
        dto.setReplies(replies.stream()
                .map(this::convertCommentWithReplies)
                .collect(Collectors.toList()));

        return dto;
    }

    @Transactional
    public void voteComment(Long commentId, Long userId, int voteValue) {
        CampaignComment comment = commentRepository.findById(commentId).orElseThrow();

        // Validate vote value
        if (voteValue != 1 && voteValue != -1 && voteValue != 0) {
            throw new IllegalArgumentException("Vote value must be 1 (upvote), -1 (downvote), or 0 (remove vote)");
        }

        if (voteValue == 0) {
            // Remove vote
            comment.getUserVotes().remove(userId);
        } else {
            // Update or add user's vote
            comment.getUserVotes().put(userId, voteValue);
        }

        // Update the likes count based on votes
        comment.setLikes(comment.getVoteScore());
        commentRepository.save(comment);
    }





    @Transactional
    public int getCommentVoteScore(Long commentId) {
        return commentRepository.findById(commentId)
                .map(CampaignComment::getVoteScore)
                .orElse(0);
    }


    public CampaignCommentDTO addComment(Long campaignId, Long userId, String text, Long parentId) {
        CrowdFunding campaign = crowdfundingRepository.findById(campaignId).orElseThrow();
        CampaignComment comment = new CampaignComment();
        comment.setCampaign(campaign);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setTimestamp(LocalDateTime.now());

        if (parentId != null) {
            CampaignComment parent = commentRepository.findById(parentId).orElseThrow();
            comment.setParent(parent);
        }
        CampaignComment savedComment = commentRepository.save(comment);
        if (!userId.equals(campaign.getCreatedBy())) {
            notificationService.createNotification(
                    campaign.getCreatedBy(),
                    "New Comment on Your Campaign",
                    "Your campaign '" + campaign.getTitle() + "' has a new comment",
                    "CAMPAIGN_COMMENT",
                    campaignId,
                    "/campaigns/" + campaignId + "/comments"
            );
        }
        return commentConverter.toDto(savedComment);
    }


    @Transactional
    public void likeComment(Long commentId, Long userId) {
        CampaignComment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CampaignComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        // First delete all replies
        List<CampaignComment> replies = commentRepository.findByParentId(commentId);
        commentRepository.deleteAll(replies);

        // Then delete the comment
        commentRepository.delete(comment);
    }

}