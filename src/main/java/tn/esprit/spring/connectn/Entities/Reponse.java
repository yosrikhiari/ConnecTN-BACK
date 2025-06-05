package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idReponse")
public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idReponse;

    @ManyToOne
    @JoinColumn(name = "id_question")
    @JsonIgnoreProperties({"test", "optionReponses"})
    Question question;

    @ManyToOne
    @JoinColumn(name = "id_option_reponse")
    @JsonIgnoreProperties("question")
    OptionReponse optionReponse;

    @ManyToOne
    @JoinColumn(name = "id")
    @JsonIgnoreProperties({"reponses", "competences", "membres", "resultats"}) // More complete

    User user;


    @ManyToOne
    @JoinColumn(name = "id_test")
    @JsonIgnoreProperties({"reponses", "questions", "resultats"})
    Test test;
}