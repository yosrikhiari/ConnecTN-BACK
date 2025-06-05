package tn.esprit.spring.connectn.Repository.groups;



import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.groups.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroup_GroupId(Long groupId);
    List<Post> findByAuthor_id(Long id);
}