package tn.esprit.spring.connectn.DTO.CROWDFUNDING;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CampaignCommentDTO {
    private Long id;
    private Long campaignId;
    private Long userId;
    private String text;
    private LocalDateTime timestamp;
    private Integer likes = 0;
    private Long parentId;
    private String username;
    private Map<Long, Integer> userVotes = new HashMap<>();
    private List<CampaignCommentDTO> replies = new ArrayList<>();

    // Manually add setter for replies to ensure it works
    public void setReplies(List<CampaignCommentDTO> replies) {
        this.replies = replies != null ? replies : new ArrayList<>();
    }
}