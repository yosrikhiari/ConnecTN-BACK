package tn.esprit.spring.connectn.Services.Interfaces;



import tn.esprit.spring.connectn.Entities.Reponse;

import java.util.List;
import java.util.Optional;

public interface IReponseService {

    List<Reponse> getAllReponses();
    Reponse addReponse(Reponse reponse);
    Reponse addReponseFull(Long idOptionReponse,Long idQuestion,Long idTest,Long idUser);
    Optional<Reponse> getReponse(Long id);
    Reponse updateReponse(Long id,Reponse reponse);
    void deleteReponse(Long id);
}