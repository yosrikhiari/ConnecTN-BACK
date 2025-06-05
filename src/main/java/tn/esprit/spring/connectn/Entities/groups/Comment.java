package tn.esprit.spring.connectn.Entities.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long commentId;

    String content;
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "author_id") // Ajout de la colonne explicitement
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    User author;

    @ManyToOne
    @JoinColumn(name = "post_id") // Ajout de la colonne explicitement
    @JsonIgnoreProperties({"author", "group", "comments"})
    Post post;
}