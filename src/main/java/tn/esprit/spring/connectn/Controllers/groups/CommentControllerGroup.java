package tn.esprit.spring.connectn.Controllers.groups;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.groups.Comment;
import tn.esprit.spring.connectn.Services.Interfaces.groups.ICommentService;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comments")
public class CommentControllerGroup {

    private final ICommentService commentService;

    public CommentControllerGroup(ICommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(comment));
    }

    @GetMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comment> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @PutMapping(value = "/{commentId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody Comment commentDetails) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentDetails));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/post/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Comment>> getCommentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUser(userId));
    }
}