package tn.esprit.spring.connectn.Repository.groups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.groups.Chat;

import java.util.Optional;
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByGroupGroupId(Long groupId);
}
