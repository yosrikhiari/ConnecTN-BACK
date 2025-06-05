package tn.esprit.spring.connectn.Entities.groups;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveSession {
    private Long liveId;
    private Long groupId;
    private Long broadcasterId;
    private String broadcasterName;
    private LocalDateTime startTime;
    private boolean active;
}