package tn.esprit.spring.connectn.Services.Interfaces.groups;


import tn.esprit.spring.connectn.Entities.groups.Comment;

import java.util.List;

public interface ICommentService {
    Comment createComment(Comment comment);
    Comment getCommentById(Long commentId);
    List<Comment> getAllComments();
    Comment updateComment(Long commentId, Comment commentDetails);
    void deleteComment(Long commentId);
    List<Comment> getCommentsByPost(Long postId);
    List<Comment> getCommentsByUser(Long userId);
}