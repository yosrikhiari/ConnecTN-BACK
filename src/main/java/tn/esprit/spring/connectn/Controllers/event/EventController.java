package tn.esprit.spring.connectn.Controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.event.Event;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.event.EventService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    // Récupérer tous les événements
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Créer un événement (tout utilisateur peut créer un événement)
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event eventRequest, @RequestParam Long userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Create a new event with only the fields from the request
        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setDate(eventRequest.getDate());
        event.setCapacity(eventRequest.getCapacity());
        event.setPoints(eventRequest.getPoints());
        event.setImages(eventRequest.getImages());
        event.setVideos(eventRequest.getVideos());

        // Let the service handle the rest
        Event createdEvent = eventService.createEvent(event, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // Récupérer un événement par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
        return ResponseEntity.ok(event);
    }

    // Mettre à jour un événement (seul l'organisateur ou un admin peut modifier)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event, @RequestParam Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }

        User user = userOptional.get();
        Event existingEvent = eventService.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        // Vérifier si l'utilisateur est l'organisateur ou un admin
        if (!existingEvent.getCreatedBy().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas la permission de modifier cet événement.");
        }

        Event updatedEvent = eventService.updateEvent(id, event, userId);
        return ResponseEntity.ok(updatedEvent);
    }

    // Supprimer un événement (seul l'organisateur ou un admin peut supprimer)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestParam Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }

        User user = userOptional.get();
        Event existingEvent = eventService.getEventById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        // Vérifier si l'utilisateur est l'organisateur ou un admin
        if (!existingEvent.getCreatedBy().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            System.out.println("Access Denied for User ID: " + user.getId() + " with Role: " + user.getRole());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // L'utilisateur est autorisé à supprimer l'événement
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<Event>> getEventsByCreator(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("Utilisateur non trouvé");
        }

        User user = userOptional.get();
        List<Event> events = eventService.getEventsByCreator(userId);
        return ResponseEntity.ok(events);
    }
}