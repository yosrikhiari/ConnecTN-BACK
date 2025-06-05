package tn.esprit.spring.connectn.Entities.CROWDFUNDING;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
public class CampaignUpdates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private CrowdFunding campaign;

    private String title;
    private String content;
    private LocalDateTime postedAt;


    private Integer viewCount = 0;

    private Boolean isMilestone = false;

    @ElementCollection
    private List<String> mediaUrls;


    private Integer likes = 0; // Initialize to 0


    private Integer shares = 0;

    @ElementCollection
    private Set<Long> likedByUsers;

    @ElementCollection
    private Set<Long> bookmarkedByUsers;

    @PrePersist
    protected void onCreate() {
        if (postedAt == null) {
            this.postedAt = LocalDateTime.now();
        }
        if (likes == null) {
            this.likes = 0;
        }
        if (viewCount == null) {
            this.viewCount = 0;
        }
    }
}