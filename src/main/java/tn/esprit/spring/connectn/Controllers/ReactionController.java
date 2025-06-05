package tn.esprit.spring.connectn.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Reaction;
import tn.esprit.spring.connectn.Services.Interfaces.ReactionService;

import java.util.Optional;

@RestController
@RequestMapping("/api/issue")
@CrossOrigin(origins = "*")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @GetMapping("/{issueId}/reaction")
    public ResponseEntity<?> getReactionByIssueAndUser(@PathVariable Long issueId, @RequestParam Long userId) {
        Optional<Reaction> reaction = reactionService.getReactionByIssueAndUser(issueId, userId);
        if (reaction.isPresent()) {
            return ResponseEntity.ok(reaction.get());
        } else {
            // Return an empty reaction object with default values instead of 404
            Reaction emptyReaction = new Reaction();
            emptyReaction.setUpvote(false);
            emptyReaction.setDownvote(false);
            emptyReaction.setUserId(userId);
            // Set a null or dummy issue reference as needed
            return ResponseEntity.ok(emptyReaction);
        }
    }

    @PutMapping("/{issueId}/upvote")
    public ResponseEntity<?> upvoteIssue(@PathVariable Long issueId, @RequestParam Long userId) {
        try {
            Reaction reaction = reactionService.upvoteIssue(issueId, userId);
            return ResponseEntity.ok(reaction);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{issueId}/downvote")
    public ResponseEntity<?> downvoteIssue(@PathVariable Long issueId, @RequestParam Long userId) {
        try {
            Reaction reaction = reactionService.downvoteIssue(issueId, userId);
            return ResponseEntity.ok(reaction);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/reaction/{reactionId}")
    public ResponseEntity<?> deleteReaction(@PathVariable Long reactionId) {
        try {
            reactionService.deleteReaction(reactionId);
            return ResponseEntity.ok(new String[]{"Reaction deleted successfully."});
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}