package tn.esprit.spring.connectn.Services.Implementation.groups;



import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.groups.Comment;
import tn.esprit.spring.connectn.Repository.groups.CommentRepositoryGroup;
import tn.esprit.spring.connectn.Services.Interfaces.groups.ICommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceGroup implements ICommentService {

    private final CommentRepositoryGroup commentRepository;

    @Override
    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment updateComment(Long commentId, Comment commentDetails) {
        Comment comment = getCommentById(commentId);
        comment.setContent(commentDetails.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPost_PostId(postId);
    }

    @Override
    public List<Comment> getCommentsByUser(Long id) {
        return commentRepository.findByAuthor_id(id);
    }
}
