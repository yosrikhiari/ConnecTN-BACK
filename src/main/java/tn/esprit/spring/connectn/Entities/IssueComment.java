package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class IssueComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

}