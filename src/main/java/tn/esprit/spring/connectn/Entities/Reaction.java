package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;
    private boolean isUpvote;
    private boolean isDownvote;
}