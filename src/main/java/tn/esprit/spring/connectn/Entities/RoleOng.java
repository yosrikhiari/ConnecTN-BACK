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

public class RoleOng {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long idRoleOng;
    String title;
    String description;
    String disponibiliteRequise;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "role_competences",
            joinColumns = @JoinColumn(name = "roleOng_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    @JsonIgnoreProperties("roleOngs")
    List<Competence> competencesRequises;

    @ManyToOne
    @JoinColumn(name = "id_organisationng")
    @JsonIgnoreProperties({"rolesDisponible", "membres"})
    OrganisationNG organisationNG;

    @OneToMany(mappedBy = "roleOng")
    @JsonIgnoreProperties({"roleOng", "user", "organisationNG"})
    private List<Membre> membres;

}