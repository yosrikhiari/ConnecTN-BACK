package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.Entities.IssueComment;

import java.util.List;

public interface CommentService {
    IssueComment createComment(Long issueId, Long userId, String content);

    List<IssueComment> getCommentsByIssueId(Long issueId);
    void deleteCommentByUserIdAndIssueId(Long userId, Long issueId);
    IssueComment updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
}

