package tn.esprit.spring.connectn.Services.Implementation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Entities.Reaction;
import tn.esprit.spring.connectn.Repository.IssueRepository;
import tn.esprit.spring.connectn.Repository.ReactionRepository;
import tn.esprit.spring.connectn.Services.Interfaces.ReactionService;

import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Optional<Reaction> getReactionByIssueAndUser(Long issueId, Long userId) {
        return reactionRepository.findByIssueIdAndUserId(issueId, userId);
    }
    @Override
    public Reaction upvoteIssue(Long issueId, Long userId) {
        // Fetch the issue
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        // Increment upvotes
        issue.setUpvotes(issue.getUpvotes() + 1);
        issueRepository.save(issue);

        // Create a new Reaction
        Reaction reaction = new Reaction();
        reaction.setIssue(issue);
        reaction.setUserId(userId);
        reaction.setUpvote(true);
        reaction.setDownvote(false);
        return reactionRepository.save(reaction);
    }

    @Override
    public Reaction downvoteIssue(Long issueId, Long userId) {
        // Fetch the issue
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        // Increment downvotes
        issue.setDownvotes(issue.getDownvotes() + 1);
        issueRepository.save(issue);

        // Create a new Reaction
        Reaction reaction = new Reaction();
        reaction.setIssue(issue);
        reaction.setUserId(userId);
        reaction.setUpvote(false);
        reaction.setDownvote(true);
        return reactionRepository.save(reaction);
    }
    @Override
    public void deleteReaction(Long reactionId) {
        // Fetch the reaction
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + reactionId));

        // Fetch the associated issue
        Issue issue = reaction.getIssue();

        // Decrement the upvote or downvote count
        if (reaction.isUpvote()) {
            issue.setUpvotes(issue.getUpvotes() - 1);
        } else if (reaction.isDownvote()) {
            issue.setDownvotes(issue.getDownvotes() - 1);
        }

        // Save the updated issue
        issueRepository.save(issue);

        // Delete the reaction
        reactionRepository.deleteById(reactionId);
    }
}