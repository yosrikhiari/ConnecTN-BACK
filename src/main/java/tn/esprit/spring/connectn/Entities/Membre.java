package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idMembre")
public class Membre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idMembre;

    @ManyToOne
    @JoinColumn(name = "id_organisationng")
    @JsonIgnoreProperties("membre")

    OrganisationNG organisationNG;

    @ManyToOne
    @JoinColumn(name = "id_roleOng")
    @JsonIgnoreProperties("membre")
    RoleOng roleOng;

    Date dateAdded;

    @ManyToOne
    @JoinColumn(name = "id")
    @JsonIgnoreProperties("membre")

    User user;

    String temoignage;

}