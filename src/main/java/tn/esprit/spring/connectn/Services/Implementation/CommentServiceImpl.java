package tn.esprit.spring.connectn.Services.Implementation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Entities.IssueComment;
import tn.esprit.spring.connectn.Repository.CommentRepository;
import tn.esprit.spring.connectn.Repository.IssueRepository;
import tn.esprit.spring.connectn.Services.Interfaces.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IssueRepository issueRepository;
    public IssueComment createComment(Long issueId, Long userId, String content) {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if (issue.isPresent()) {
            IssueComment comment = new IssueComment();
            comment.setIssue(issue.get());
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        } else {
            throw new EntityNotFoundException("Issue not found with id: " + issueId);
        }
    }
    public List<IssueComment> getCommentsByIssueId(Long issueId) {
        return commentRepository.findByIssueId(issueId);
    }
    @Override
    @Transactional
    public void deleteCommentByUserIdAndIssueId(Long userId, Long issueId) {
    }
    @Override
    public IssueComment updateComment(Long commentId, String content) {
        IssueComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
