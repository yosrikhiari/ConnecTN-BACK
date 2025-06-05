package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.*;
import tn.esprit.spring.connectn.Repository.*;
import tn.esprit.spring.connectn.Services.Interfaces.IReponseService;

import java.util.List;
import java.util.Optional;

@Service
public class ReponseService implements IReponseService {

    @Autowired
    private IReponseRepository reponseRepository;

    @Autowired
    private IQuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ITestRepository testRepository;

    @Autowired
    private IOptionReponseRepository optionReponseRepository;


    @Override
    public List<Reponse> getAllReponses() {
        return reponseRepository.findAll();
    }

    @Override
    public Reponse addReponse(Reponse reponse) {
        return reponseRepository.save(reponse);
    }

    @Override
    public Reponse addReponseFull(Long idOptionReponse, Long idQuestion, Long idTest, Long idUser) {
        OptionReponse optionReponse = optionReponseRepository.findById(idOptionReponse)
                .orElseThrow(() -> new RuntimeException("OptionReponse not found"));

        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reponse reponse = new Reponse();
        reponse.setOptionReponse(optionReponse);
        reponse.setQuestion(question);
        reponse.setTest(test);
        reponse.setUser(user);

        return reponseRepository.save(reponse);
    }

    @Override
    public Optional<Reponse> getReponse(Long id) {
        return reponseRepository.findById(id);
    }

    @Override
    public Reponse updateReponse(Long id, Reponse reponse) {
        Reponse reponse1 = reponseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reponse not found"));
        reponse1.setUser(reponse.getUser());
        reponse1.setQuestion(reponse.getQuestion());
        reponse1.setOptionReponse(reponse.getOptionReponse());
        return reponseRepository.save(reponse1);
    }

    @Override
    public void deleteReponse(Long id) {
        Reponse reponse1 = reponseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reponse not found"));
        reponseRepository.delete(reponse1);
    }
}




