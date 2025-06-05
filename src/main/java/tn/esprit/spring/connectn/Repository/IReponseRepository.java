package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.Reponse;

import java.util.List;
import java.util.Optional;

public interface IReponseRepository extends JpaRepository<Reponse, Long> {
    Optional <Reponse> findById(Long id);
    @Modifying
    @Query("DELETE FROM Reponse r WHERE r.test.idTest = :id")
    void deleteByTestId(@Param("id") Long id);}