package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Competence;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Repository.ICompetenceRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Interfaces.ICompetenceService;

import java.util.List;
import java.util.Optional;
@Service
public class CompetenceService implements ICompetenceService {

    @Autowired
    private ICompetenceRepository competenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Competence> getAllCompetences() {
        return competenceRepository.findAll();
    }

    @Override
    public Competence addCompetence(Competence competence,Long idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        competence.setUser(user);
        return competenceRepository.save(competence);
    }

    public Competence addCompetenceOng(Competence competence) {
        return competenceRepository.save(competence);
    }

    @Override
    public Optional<Competence> findCompetenceById(Long id) {
        return competenceRepository.findById(id);
    }

    @Override
    public Competence updateCompetence(Long id,Competence competence) {
        Competence competence1=competenceRepository.findById(id).orElseThrow(()->new RuntimeException("No such competence"));
        competence1.setNameCompetence(competence.getNameCompetence());
        competence1.setNiveauCompetence(competence.getNiveauCompetence());
        competence1.setUser(competence.getUser());
        competence1.setCategorie(competence.getCategorie());
        return competenceRepository.save(competence1);
    }

    @Override
    public void deleteCompetence(Long id) {
        Competence competence=competenceRepository.findById(id).orElseThrow(()->new RuntimeException("No such competence"));
        competenceRepository.delete(competence);
    }
}