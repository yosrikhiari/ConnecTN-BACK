package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.Membre;

import java.util.Optional;

public interface IMembreRepository extends JpaRepository<Membre, Long> {

    Optional <Membre> findById(Long id);
    @Modifying
    @Query("DELETE FROM Membre m WHERE m.roleOng.idRoleOng = :roleOngId")
    void deleteByRoleId(@Param("roleOngId") Long roleOngId);
}