package tn.esprit.spring.connectn.Entities.groups;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long chatId;

    String name;

    @OneToOne
    @JoinColumn(name = "group_id")
    Group group;

    @OneToMany(mappedBy = "chat" , cascade = CascadeType.ALL)
    List<Message> messages = new ArrayList<>();
}