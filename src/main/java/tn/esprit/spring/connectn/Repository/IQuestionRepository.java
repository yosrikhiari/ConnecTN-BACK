package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.connectn.Entities.Question;

import java.util.Optional;

public interface IQuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findById(Long id);
}