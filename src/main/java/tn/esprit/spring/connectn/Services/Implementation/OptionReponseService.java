package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.OptionReponse;
import tn.esprit.spring.connectn.Entities.Question;
import tn.esprit.spring.connectn.Repository.IOptionReponseRepository;
import tn.esprit.spring.connectn.Repository.IQuestionRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IOptionReponseService;

import java.util.List;
import java.util.Optional;

@Service
public class OptionReponseService implements IOptionReponseService {

    @Autowired
    private IOptionReponseRepository optionReponseRepository;
    @Autowired
    private IQuestionRepository questionRepository;

    @Override
    public List<OptionReponse> getAllOptionReponses() {
        return optionReponseRepository.findAll();
    }

    @Override
    public OptionReponse addOptionReponse(OptionReponse optionReponse) {
        return optionReponseRepository.save(optionReponse);
    }

    @Override
    public OptionReponse addOptionReponseFull(Long idQuestion, String text, Long valeur) {
        Question question = questionRepository.findById(idQuestion)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        OptionReponse optionReponse = new OptionReponse();
        optionReponse.setText(text);
        optionReponse.setValeur(valeur);
        optionReponse.setQuestion(question);

        return optionReponseRepository.save(optionReponse);
    }

    @Override
    public Optional<OptionReponse> findOptionReponseById(Long id) {
        return optionReponseRepository.findById(id);
    }

    @Override
    public OptionReponse updateOptionReponse(Long id, OptionReponse optionReponse) {
        OptionReponse optionReponse1 = optionReponseRepository.findById(id).orElseThrow(()->new RuntimeException("OptionReponse not found"));
        optionReponse1.setQuestion(optionReponse.getQuestion());
        optionReponse1.setText(optionReponse.getText());
        optionReponse1.setValeur(optionReponse.getValeur());
        return optionReponseRepository.save(optionReponse1);
    }

    @Override
    public void deleteOptionReponse(Long id) {
        OptionReponse optionReponse = optionReponseRepository.findById(id).orElseThrow(()->new RuntimeException("OptionReponse not found"));
        optionReponseRepository.delete(optionReponse);

    }
}