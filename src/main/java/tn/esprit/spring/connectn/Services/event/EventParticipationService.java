package tn.esprit.spring.connectn.Services.event;

import tn.esprit.spring.connectn.Entities.event.Event;
import tn.esprit.spring.connectn.Entities.event.EventParticipation;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Services.Implementation.UserService;
import tn.esprit.spring.connectn.Repository.event.EventParticipationRepository;
import tn.esprit.spring.connectn.Repository.event.EventRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import tn.esprit.spring.connectn.Entities.User;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.Repository.event.EventParticipationRepository;
import tn.esprit.spring.connectn.Repository.event.EventRepository;
import java.util.List;
import java.util.Optional;

@Service
public class EventParticipationService {

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService; // Inject an instance of EventService

    public EventParticipation participateInEvent(Long userId, Long eventId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (userOpt.isPresent() && eventOpt.isPresent()) {
            User user = userOpt.get();
            Event event = eventOpt.get();

            // Vérifier si l'événement a atteint sa capacité maximale
            if (event.getParticipations().size() >= event.getCapacity()) {
                throw new RuntimeException("L'événement a atteint sa capacité maximale.");
            }

            // Vérifier si l'utilisateur participe déjà à cet événement
            if (event.getParticipations().stream().anyMatch(p -> p.getUser().getId().equals(userId))) {
                throw new RuntimeException("L'utilisateur participe déjà à cet événement.");
            }

            EventParticipation participation = new EventParticipation();
            participation.setUser(user);
            participation.setEvent(event);
            participation.setPointsEarned(event.getPoints()); // Les points gagnés sont ceux de l'événement

            EventParticipation savedParticipation = eventParticipationRepository.save(participation);

            // Augmenter les points de l'utilisateur
            user.setPoints(user.getPoints() + event.getPoints());
            userRepository.save(user);

            // Réduire la capacité de l'événement
            event.setCapacity(event.getCapacity() - 1); // Décrémenter la capacité
            eventRepository.save(event); // Sauvegarder la mise à jour

            // Mettre à jour le statut de l'événement si nécessaire
            eventService.updateEventStatusIfFull(event); // Appel de la méthode sur l'instance injectée


            return savedParticipation;
        } else {
            throw new RuntimeException("Utilisateur ou événement non trouvé.");
        }
    }

    public List<EventParticipation> getAllParticipations() {
        return eventParticipationRepository.findAll();
    }
    @Transactional
    public void deleteByUserAndEvent(Long userId, Long eventId) {
        final Logger logger = LoggerFactory.getLogger(EventParticipationService.class);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (eventOpt.isPresent() && userOpt.isPresent()) {
            Event event = eventOpt.get();
            User user = userOpt.get();

            // 1. First delete the participation record
            eventParticipationRepository.deleteByUserIdAndEventId(userId, eventId);

            // 2. Handle points deduction only if event has positive points
            if (event.getPoints() > 0) {
                try {
                    int updatedPoints = Math.max(0, user.getPoints() - event.getPoints()); // éviter des points négatifs
                    user.setPoints(updatedPoints);
                    userRepository.save(user);
                } catch (Exception e) {
                    logger.warn("Could not deduct points for user {}: {}", userId, e.getMessage());
                }
            }


            // 3. Increase event capacity
            event.setCapacity(event.getCapacity() + 1);

            // 4. Save event with capacity update
            try {
                eventRepository.save(event);
            } catch (ConstraintViolationException e) {
                throw new RuntimeException("Could not update event capacity: " + e.getMessage());
            }

            // 5. Update event status
            eventService.updateEventStatusIfFull(event);
        } else {
            throw new RuntimeException("Utilisateur ou événement non trouvé.");
        }
    }
    // Correction: Ajoutez cette méthode si elle n'existe pas
    public Optional<EventParticipation> findByUserAndEvent(Long userId, Long eventId) {
        return eventParticipationRepository.findByUser_IdAndEvent_Id(userId, eventId);
    }
    public List<EventParticipation> getParticipationsByEventId(Long eventId) {
        return eventParticipationRepository.findByEvent_Id(eventId);
    }
    public List<EventParticipation> getUserParticipations(Long userId) {
        return eventParticipationRepository.findByUser_Id(userId);
    }
}
