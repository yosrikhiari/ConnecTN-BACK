package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.RoleOng;

import java.util.Optional;

public interface IRoleOngRepository extends JpaRepository<RoleOng, Long> {
    Optional <RoleOng> findById(Long id);

}