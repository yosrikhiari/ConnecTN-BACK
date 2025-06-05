package tn.esprit.spring.connectn.Services.Implementation;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Question;
import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleCascadeService {
    private final IRoleOngRepository roleRepository;
    private final ITestRepository testRepository;
    private final IReponseRepository reponseRepository;
    private final IQuestionRepository questionRepository;
    private final IOptionReponseRepository optionReponseRepository;
    private final IResultatRepository resultatRepository;
    private final IMembreRepository membreRepository;

    @Transactional
    public void deleteRoleWithDependencies(Long roleId) {
        // 1. Trouver le rôle
        RoleOng role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        // 2. Supprimer les membres associés au rôle
        membreRepository.deleteByRoleId(roleId);

        // 3. Récupérer tous les tests associés
        List<Test> tests = testRepository.findByRoleId(roleId);

        // 4. Pour chaque test, supprimer les dépendances
        for (Test test : tests) {
            // a. Supprimer les résultats associés au test
            resultatRepository.deleteByTestId(test.getIdTest());

            // b. Supprimer les réponses
            reponseRepository.deleteByTestId(test.getIdTest());

            // c. Supprimer les questions et leurs options
            if (test.getQuestions() != null) {
                for (Question question : test.getQuestions()) {
                    optionReponseRepository.deleteByQuestionId(question.getIdQuestion());
                    questionRepository.delete(question);
                }
            }

            // d. Supprimer le test
            testRepository.delete(test);
        }

        // 5. Supprimer les associations de compétences
        role.getCompetencesRequises().clear();
        roleRepository.save(role);

        // 6. Finalement supprimer le rôle
        roleRepository.delete(role);
    }
}