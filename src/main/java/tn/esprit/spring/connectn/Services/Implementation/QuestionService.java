package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Question;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Repository.IQuestionRepository;
import tn.esprit.spring.connectn.Repository.ITestRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IQuestionService;

import java.util.List;
import java.util.Optional;
@Service
public class QuestionService implements IQuestionService {

    @Autowired
    private IQuestionRepository questionRepository;

    @Autowired
    private ITestRepository testRepository;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll() ;
    }

    @Override
    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question addQuestionFull(Long idTest, String enonce, String categorie) {
        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        Question question = new Question();
        question.setEnonce(enonce);
        question.setCategorie(categorie);
        question.setTest(test);

        return questionRepository.save(question);
    }

    @Override
    public Optional<Question> getQuestionById(long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question updateQuestion(Long id, Question question) {
        Question question1=questionRepository.findById(id).orElseThrow(()->new RuntimeException("Question not found"));
        question1.setCategorie(question.getCategorie());
        question1.setTest(question.getTest());
        question1.setEnonce(question.getEnonce());
        question1.setOptionReponses(question.getOptionReponses());
        return questionRepository.save(question1);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question1=questionRepository.findById(id).orElseThrow(()->new RuntimeException("Question not found"));
        questionRepository.delete(question1);
    }
}