package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.Resultat;

import java.util.Optional;

public interface IResultatRepository extends JpaRepository<Resultat, Long> {
    Optional<Resultat> findById(Long id);
    @Modifying
    @Query("DELETE FROM Resultat r WHERE r.test.idTest = :testId")
    void deleteByTestId(@Param("testId") Long testId);
}