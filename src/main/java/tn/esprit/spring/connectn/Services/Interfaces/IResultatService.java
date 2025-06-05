package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.Resultat;

import java.util.List;
import java.util.Optional;

public interface IResultatService {
    List<Resultat> getAllResultats();
    Resultat addResultat(Resultat resultat);
    Optional<Resultat> getResultatById(Long id);
    Resultat addResultatFull( Double score, Long idUser, Long idTest);
    Resultat updateResultat(Long id,Resultat resultat);
    void deleteResultat(Long id);
}