package tn.esprit.spring.connectn.Entities.groups;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.spring.connectn.Entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups_m")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "groupId")
public class Group {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long groupId;

     @Column(nullable = false)
     String name;

     String description;
     String category;

     @Enumerated(EnumType.STRING)
     GroupType type;

     String coverPhoto;
     String groupImage;

     LocalDateTime createdDate;

     @ElementCollection
     List<String> rules;

     @ManyToOne
     @JoinColumn(name = "admin_id")
     // Supprimer les propriétés qui n'existent pas dans la nouvelle classe User
     @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
     User admin;

     @ManyToMany
     @JoinTable(
             name = "group_members",
             joinColumns = @JoinColumn(name = "group_id"),
             inverseJoinColumns = @JoinColumn(name = "user_id")
     )
     // Supprimer les propriétés qui n'existent pas dans la nouvelle classe User
     @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
     List<User> members = new ArrayList<>();

     @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
     List<Post> posts = new ArrayList<>();

     @OneToOne(mappedBy = "group", cascade = CascadeType.ALL)
     @JsonIgnoreProperties("messages")
     Chat groupChat;

     @ElementCollection
     @CollectionTable(name = "group_join_requests",
             joinColumns = @JoinColumn(name = "group_id"))
     @MapKeyJoinColumn(name = "user_id")
     @Column(name = "status")
     @JsonIgnore
     Map<User, JoinStatus> pendingRequests = new HashMap<>();


}