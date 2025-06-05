package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Services.Interfaces.CommentService;

@RestController
@RequestMapping("/api/")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("comments/create")
    public ResponseEntity<?> createComment(@RequestParam Long issueId, @RequestParam Long userId, @RequestParam String content) {
        try {
            return ResponseEntity.ok(commentService.createComment(issueId, userId, content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @GetMapping("comments/{issueId}")
    public ResponseEntity<?> getCommentsByIssueId(@PathVariable Long issueId) {
        try {
            return ResponseEntity.ok(commentService.getCommentsByIssueId(issueId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching comments");
        }
    }

    //bedelha by comment id
    @DeleteMapping("comments/delete/{issueId}/{userId}")
    public ResponseEntity<?> deleteCommentByUserIdAndIssueId(@PathVariable Long issueId, @PathVariable Long userId) {
        commentService.deleteCommentByUserIdAndIssueId(userId, issueId);
        return ResponseEntity.ok("Comment deleted successfully");
    }
    @PutMapping("comments/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestParam String content) {
        try {
            return ResponseEntity.ok(commentService.updateComment(commentId, content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("comments/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}