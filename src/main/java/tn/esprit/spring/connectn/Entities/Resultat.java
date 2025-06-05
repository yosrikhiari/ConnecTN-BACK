package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idResultat")
public class Resultat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idResultat;

    Date dateAdded;

    Double score; // 🔥 Ajout du score global du test

    @OneToMany(mappedBy = "resultat",cascade = CascadeType.ALL)
    List<RoleRecommande> roleRecommandes;

    @ManyToOne
    @JoinColumn(name = "id")
    @JsonIgnoreProperties({"reponses", "competences", "membres", "resultats"})
    User user;


    @ManyToOne
    @JoinColumn(name = "id_test")
    @JsonIgnoreProperties({"reponses", "questions", "resultats"})
    Test test;
}