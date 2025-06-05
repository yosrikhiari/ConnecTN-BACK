package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long reporterId;
    private Long assigneeId;
    @Enumerated(EnumType.STRING)
    private IssueCategory category;
    @Enumerated(EnumType.STRING)
    private IssueStatus status;
    private int upvotes;
    private int downvotes;
    private boolean isVerified;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;

    @ElementCollection
    @CollectionTable(name = "issue_media", joinColumns = @JoinColumn(name = "issue_id"))
    @MapKeyColumn(name = "public_id")
    @Column(name = "url")
    private Map<String, String> mediaPaths; // Key: publicId, Value: URL
}
