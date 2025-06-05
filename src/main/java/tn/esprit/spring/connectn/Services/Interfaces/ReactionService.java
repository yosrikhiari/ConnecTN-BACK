package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.Entities.Reaction;

import java.util.Optional;

public interface ReactionService {
    Reaction upvoteIssue(Long issueId, Long userId);
    Reaction downvoteIssue(Long issueId, Long userId);
    Optional<Reaction> getReactionByIssueAndUser(Long issueId, Long userId);
    void deleteReaction(Long reactionId);
}