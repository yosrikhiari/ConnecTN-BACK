package tn.esprit.spring.connectn.Repository.event;

import tn.esprit.spring.connectn.Entities.event.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.User;
import java.util.Optional;
import java.util.List;
import tn.esprit.spring.connectn.Entities.event.Event;
@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    boolean existsByUserAndEvent(User user, Event event);
    List<EventParticipation> findByUser_Id(Long userId);
    Optional<EventParticipation> findByUser_IdAndEvent_Id(Long userId, Long eventId);
    void deleteByUserIdAndEventId(Long userId, Long eventId);
    List<EventParticipation> findByEvent_Id(Long eventId);
}
