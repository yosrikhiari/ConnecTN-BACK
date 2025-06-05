package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.Competence;

import java.util.List;
import java.util.Optional;

public interface ICompetenceService {

    List<Competence>getAllCompetences();
    Competence addCompetence(Competence competence,Long idUser);
    Competence addCompetenceOng(Competence competence);
    Optional<Competence> findCompetenceById(Long id);
    Competence updateCompetence(Long id,Competence competence);
    void  deleteCompetence(Long id);

}