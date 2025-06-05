package tn.esprit.spring.connectn.Repository.event;


import tn.esprit.spring.connectn.Entities.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Services.event.ChatbotService;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(User user);

    List<Event> findByDateAfterOrderByDateAsc(LocalDateTime date);
    // Modify the repository method to return a single event (Optional)
    Optional<Event> findByTitle(String title);

}

