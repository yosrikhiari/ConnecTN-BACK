 package tn.esprit.spring.connectn.DTO.groups;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long messageId;
    private String content;
    private LocalDateTime timestamp;
    private Long senderId;
    private String senderName;
    private Long chatId;
    private Long groupId;
}
