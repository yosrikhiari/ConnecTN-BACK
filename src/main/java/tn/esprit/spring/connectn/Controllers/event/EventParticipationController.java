package tn.esprit.spring.connectn.Controllers.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.eventDto.EventParticipationDTO;
import tn.esprit.spring.connectn.Entities.event.EventParticipation;
import tn.esprit.spring.connectn.Repository.event.EventParticipationRepository;
import tn.esprit.spring.connectn.Services.event.EventParticipationService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/event-participations")
public class EventParticipationController {

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @DeleteMapping("/{userId}/{eventId}")
    public ResponseEntity<?> deleteParticipation(@PathVariable Long userId, @PathVariable Long eventId) {
        eventParticipationService.deleteByUserAndEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EventParticipationDTO>> getAllParticipations() {
        List<EventParticipation> participations = eventParticipationService.getAllParticipations();

        List<EventParticipationDTO> dtoList = participations.stream()
                .map(participation -> new EventParticipationDTO(
                        participation.getUser().getId(),
                        participation.getUser().getUsername(),
                        participation.getEvent().getId(),
                        participation.getEvent().getTitle(),
                        participation.getPointsEarned()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{userId}/{eventId}")
    public ResponseEntity<EventParticipation> participate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            HttpServletRequest request) {

        System.out.println("=== HEADERS ===");
        Collections.list(request.getHeaderNames())
                .forEach(h -> System.out.println(h + ": " + request.getHeader(h)));

        System.out.println("=== TENTATIVE PARTICIPATION ===");
        System.out.println("User ID: " + userId);
        System.out.println("Event ID: " + eventId);

        try {
            EventParticipation participation = eventParticipationService.participateInEvent(userId, eventId);
            System.out.println("Participation créée avec ID: " + participation.getId());
            return ResponseEntity.ok(participation);
        } catch (Exception e) {
            System.out.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/participations/{userId}/{eventId}")
    public ResponseEntity<EventParticipation> getParticipation(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        Optional<EventParticipation> participation = eventParticipationRepository
                .findByUser_IdAndEvent_Id(userId, eventId);
        return participation
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventParticipationDTO>> getEventParticipants(@PathVariable Long eventId) {
        List<EventParticipation> participations = eventParticipationService.getParticipationsByEventId(eventId);

        List<EventParticipationDTO> dtoList = participations.stream()
                .map(p -> new EventParticipationDTO(
                        p.getUser().getId(),
                        p.getUser().getUsername(),
                        p.getEvent().getId(),
                        p.getEvent().getTitle(),
                        p.getPointsEarned()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventParticipationDTO>> getUserParticipations(@PathVariable Long userId) {
        List<EventParticipation> participations = eventParticipationService.getUserParticipations(userId);

        List<EventParticipationDTO> dtoList = participations.stream()
                .map(p -> new EventParticipationDTO(
                        p.getUser().getId(),
                        p.getUser().getUsername(),
                        p.getEvent().getId(),
                        p.getEvent().getTitle(),
                        p.getPointsEarned())) // Ajout de la date de participation
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}