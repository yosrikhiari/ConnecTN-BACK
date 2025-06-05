package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keycloakId;

    private String username;
    private String firstName;
    private String lastName;
    private String emailAddress;

    private int points = 0;
    private int phoneNumber;
    private Date birthdate;
    private String city;
    private String image;
    private String aboutMe;
    @Enumerated(EnumType.STRING)
    private Role role;




}