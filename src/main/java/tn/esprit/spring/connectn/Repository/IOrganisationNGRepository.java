package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.OrganisationNG;

import java.util.Optional;

public interface IOrganisationNGRepository extends JpaRepository<OrganisationNG, Long> {
    Optional <OrganisationNG> findById(Long id);
}