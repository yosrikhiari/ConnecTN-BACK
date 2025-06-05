package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignCommentDTO;
import tn.esprit.spring.connectn.Services.CROWDFUNDING.CampaignCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/comments")
public class CampaignCommentController {

    @Autowired
    private CampaignCommentService commentService;

    @GetMapping
    public List<CampaignCommentDTO> getComments(
            @PathVariable Long campaignId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getCommentsByCampaign(campaignId);
    }

    @PostMapping
    public ResponseEntity <CampaignCommentDTO> addComment(
            @PathVariable Long campaignId,
            @RequestParam Long userId,
            @RequestParam String text,
            @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(
                commentService.addComment(campaignId, userId, text, parentId)
        );
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long campaignId,
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/comments/{commentId}/score")
    public ResponseEntity<Integer> getCommentScore(@PathVariable Long commentId) {
        int score = commentService.getCommentVoteScore(commentId);
        return ResponseEntity.ok(score);
    }

    @PostMapping("/{commentId}/vote")
    public ResponseEntity<Integer> voteComment(
            @PathVariable Long campaignId,
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam int voteValue) {
        // Validate vote value
        if (voteValue != 1 && voteValue != -1 && voteValue != 0) {
            return ResponseEntity.badRequest().build();
        }

        commentService.voteComment(commentId, userId, voteValue);
        int newScore = commentService.getCommentVoteScore(commentId);
        return ResponseEntity.ok(newScore);
    }



    @GetMapping("/{commentId}/score")
    public ResponseEntity<Integer> getCommentScore(
            @PathVariable Long campaignId,
            @PathVariable Long commentId) {
        int score = commentService.getCommentVoteScore(commentId);
        return ResponseEntity.ok(score);
    }
}