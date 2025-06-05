package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.OptionReponse;

import java.util.List;
import java.util.Optional;

public interface IOptionReponseService {

    List<OptionReponse> getAllOptionReponses();
    OptionReponse addOptionReponse(OptionReponse optionReponse);
    OptionReponse addOptionReponseFull(Long idQuestion, String text,Long valeur);
    Optional<OptionReponse> findOptionReponseById(Long id);
    OptionReponse updateOptionReponse(Long id,OptionReponse optionReponse);
    void deleteOptionReponse(Long id);
}