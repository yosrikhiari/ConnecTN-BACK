package tn.esprit.spring.connectn.Entities.groups;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.connectn.Entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long postId;

    String content;
    String mediaUrl;

    @Enumerated(EnumType.STRING)
    FileType mediaType;

    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id") // Ajout de la colonne explicitement
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    User author;

    @ManyToOne
    @JoinColumn(name = "group_id") // Ajout de la colonne explicitement
    @JsonIgnoreProperties({"posts", "members", "admin", "pendingRequests"})
    Group group;

    Integer likeCount = 0;

    Boolean isLikedByCurrentUser = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<Comment> comments = new ArrayList<>();
}