package tn.esprit.spring.connectn.Repository.groups;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.groups.Comment;

import java.util.List;

public interface CommentRepositoryGroup extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostId(Long postId);
    List<Comment> findByAuthor_id(Long id);
}