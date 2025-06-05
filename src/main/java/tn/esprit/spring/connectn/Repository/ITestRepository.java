package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.Test;

import java.util.List;
import java.util.Optional;

public interface ITestRepository extends JpaRepository<Test, Long> {
    Optional <Test> findById(Long id);
    @Query("SELECT t FROM Test t WHERE t.roleOng.idRoleOng = :roleOngId")
    List<Test> findByRoleId(@Param("roleOngId") Long roleOngId);}