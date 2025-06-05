package tn.esprit.spring.connectn.Entities.groups;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.connectn.Entities.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long messageId;

    String content;
    LocalDateTime timestamp;

    boolean isRead;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    User sender;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    Chat chat;
}