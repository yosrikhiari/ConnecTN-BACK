package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.OptionReponse;

import java.util.List;
import java.util.Optional;

public interface IOptionReponseRepository extends JpaRepository<OptionReponse, Long> {

    Optional<OptionReponse> findById(Long id);
    @Modifying
    @Query("DELETE FROM OptionReponse o WHERE o.question.idQuestion = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}
