package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.Question;

import java.util.List;
import java.util.Optional;

public interface IQuestionService {

    List<Question> getAllQuestions();
    Question addQuestion(Question question);
    Question addQuestionFull(Long idTest,String enonce,String categorie);
    Optional<Question> getQuestionById(long id);
    Question updateQuestion(Long id, Question question);
    void deleteQuestion(Long id);
}