package tn.esprit.spring.connectn.Entities.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.event.EventStatus;
import tn.esprit.spring.connectn.Entities.event.EventParticipation;

@Entity
@Table(name = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"participations"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation ManyToOne avec l'utilisateur (organisateur)
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;  // L'utilisateur qui a créé l'événement (organisateur)

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, message = "Le titre doit contenir au moins 3 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "L'emplacement est obligatoire")
    private String location;

    @Positive(message = "Les points doivent être positifs")
    private int points; // Points gagnés pour la participation

    @Positive(message = "La capacité doit être positive")
    private int capacity; // Nombre maximum de participants

    @Future(message = "La date doit être dans le futur")
    private LocalDateTime date;

    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private List<String> images;

    @ElementCollection
    @CollectionTable(name = "event_videos", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "video_url")
    private List<String> videos;

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PENDING;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipation> participations;
}
