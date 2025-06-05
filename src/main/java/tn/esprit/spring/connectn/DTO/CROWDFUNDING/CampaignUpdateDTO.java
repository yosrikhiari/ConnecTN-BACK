package tn.esprit.spring.connectn.DTO.CROWDFUNDING;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CampaignUpdateDTO {
    private Long id;
    private Long campaignId;
    private String title;
    private String content;
    private LocalDateTime postedAt;
    private Integer viewCount = 0;
    private Boolean isMilestone = false;
    private List<String> mediaUrls;
    private Integer likes = 0;
    private Integer shares = 0;
    private Set<Long> likedByUsers;
    private Set<Long> bookmarkedByUsers;
    private boolean userLiked;
    private boolean userBookmarked;
}