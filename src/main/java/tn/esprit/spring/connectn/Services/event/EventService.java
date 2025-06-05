package tn.esprit.spring.connectn.Services.event;

import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.event.Event;
import tn.esprit.spring.connectn.Entities.event.EventStatus;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Repository.event.EventRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get the user by ID and handle role assignment
    private User getUserAndAssignRoleIfNeeded(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // If the user is not an admin, assign them the 'organizer' role
        if (!user.getRole().name().equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.USER);
            userRepository.save(user);  // Save the updated user
        }
        return user;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event, Long userId) {
        // Retrieve the user and assign the role if necessary
        User user = getUserAndAssignRoleIfNeeded(userId);

        // Assign the user as the creator of the event
        event.setCreatedBy(user);

        // Save and return the created event
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Long id, Event eventDetails, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Update event details
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setLocation(eventDetails.getLocation());
        event.setDate(eventDetails.getDate());
        event.setCapacity(eventDetails.getCapacity());
        event.setPoints(eventDetails.getPoints());
        event.setImages(eventDetails.getImages());
        event.setVideos(eventDetails.getVideos());

        // Retrieve the user and set as the creator
        User user = getUserAndAssignRoleIfNeeded(userId);
        event.setCreatedBy(user);

        // Save and return the updated event
        return eventRepository.save(event);
    }

    public void updateEventStatusIfFull(Event event) {
        // Update event status if capacity is reached
        if (event.getCapacity() <= 0 && event.getStatus() != EventStatus.CANCELED) {
            event.setStatus(EventStatus.APPROVED);  // Change to approved or another status
            eventRepository.save(event);
        } else if (event.getCapacity() > 0 && event.getStatus() == EventStatus.APPROVED) {
            event.setStatus(EventStatus.PENDING);  // Revert to pending if capacity is available
            eventRepository.save(event);
        }
    }
    public List<Event> getEventsByCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return eventRepository.findByCreatedBy(user);
    }
}
