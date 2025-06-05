package tn.esprit.spring.connectn.Entities.groups;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveComment {
    private Long commentId;
    private String content;
    private Long senderId;
    private String senderName;
    private Long liveId;
    private LocalDateTime timestamp;
}