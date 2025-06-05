package tn.esprit.spring.connectn.Entities.CROWDFUNDING;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @Setter
public class CampaignComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CrowdFunding campaign;

    private Long userId;
    private String text;
    private LocalDateTime timestamp;
    private Integer likes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private CampaignComment parent;

    @ElementCollection
    @CollectionTable(name = "comment_votes", joinColumns = @JoinColumn(name = "comment_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "vote_value")
    private Map<Long, Integer> userVotes = new HashMap<>(); // 1 for upvote, -1 for downvote

    public int getVoteScore() {
        return userVotes.values().stream().mapToInt(Integer::intValue).sum();
    }
}
