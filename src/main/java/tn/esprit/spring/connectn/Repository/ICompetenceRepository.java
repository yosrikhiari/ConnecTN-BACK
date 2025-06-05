package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.Competence;

import java.util.Optional;

public interface ICompetenceRepository extends JpaRepository<Competence, Long> {

    Optional <Competence> findById(Long id);
}