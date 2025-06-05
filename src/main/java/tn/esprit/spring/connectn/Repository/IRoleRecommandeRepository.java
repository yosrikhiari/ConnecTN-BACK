package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.RoleRecommande;

import java.util.Optional;

public interface IRoleRecommandeRepository extends JpaRepository<RoleRecommande, Long> {
    Optional <RoleRecommande> findById(Long id);
}