package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Resultat;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Repository.IResultatRepository;
import tn.esprit.spring.connectn.Repository.ITestRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IResultatService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResultatService  implements IResultatService {

    @Autowired
    private IResultatRepository resultatRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ITestRepository testRepository;

    @Override
    public List<Resultat> getAllResultats() {
        return resultatRepository.findAll() ;
    }

    @Override
    public Resultat addResultat(Resultat resultat) {
        return resultatRepository.save(resultat);
    }

    @Override
    public Optional<Resultat> getResultatById(Long id) {
        return resultatRepository.findById(id);
    }

    @Override
    public Resultat addResultatFull(Double score, Long idUser, Long idTest) {
        Resultat resultat = new Resultat();
        resultat.setDateAdded(new Date());
        resultat.setScore(score);

        // Récupération des entités User et Test par ID
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        resultat.setUser(user);
        resultat.setTest(test);

        return resultatRepository.save(resultat);
    }

    @Override
    public Resultat updateResultat(Long id, Resultat resultat) {
        Resultat resultat1 = resultatRepository.findById(id).orElseThrow(()-> new RuntimeException("Resultat not found"));
        resultat1.setUser(resultat.getUser());
        resultat1.setDateAdded(resultat.getDateAdded());
        resultat1.setRoleRecommandes(resultat.getRoleRecommandes());
        return resultatRepository.save(resultat1);
    }

    @Override
    public void deleteResultat(Long id) {
        Resultat resultat1 = resultatRepository.findById(id).orElseThrow(()-> new RuntimeException("Resultat not found"));
        resultatRepository.delete(resultat1);

    }


}