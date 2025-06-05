package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
        property = "idTest")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idTest;

    String name;

    @ManyToOne
    @JoinColumn(name = "roleOng_id")
    @JsonIgnoreProperties({"tests", "competencesRequises", "membres"})
    private RoleOng roleOng;

    @OneToMany(mappedBy = "test",cascade = CascadeType.ALL)
    @JsonIgnoreProperties("test")
    List<Question>questions;


    @OneToMany(mappedBy = "test",cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"test", "question", "optionReponse"})
    List<Reponse>reponses;


    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("test")

    List<Reponse>resultats;

}