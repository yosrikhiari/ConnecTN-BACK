package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganisationNG {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idOrganisationNG;
    String name;
    String description;
    String mission;
    String region;
    String domaineAction;
    String taille;
    String contact;
    String imageUrl;

    @OneToMany(mappedBy = "organisationNG", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("organisationNG")
    List<RoleOng> rolesDisponible;

    @OneToMany(mappedBy = "organisationNG", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("organisationNG")
    List<Membre> membres;
}