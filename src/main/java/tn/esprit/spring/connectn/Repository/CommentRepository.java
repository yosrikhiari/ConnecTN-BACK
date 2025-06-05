package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.IssueComment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<IssueComment, Long> {

    List<IssueComment> findByIssueId(Long issueId);
    void deleteByIssueId(Long issueId);
    void deleteByUserIdAndIssueId(Long userId, Long issueId);
}