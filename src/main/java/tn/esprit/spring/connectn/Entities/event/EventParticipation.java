package tn.esprit.spring.connectn.Entities.event;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.spring.connectn.Entities.User;
import java.time.LocalDateTime;
@Entity
@Table(name = "event_participations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private int pointsEarned = 0; // Field to store points earned, initialized to 0
    // With Lombok's @Setter, the setPointsEarned(int) method will be generated.
}