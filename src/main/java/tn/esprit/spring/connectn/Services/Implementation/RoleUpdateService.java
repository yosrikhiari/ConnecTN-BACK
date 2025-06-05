package tn.esprit.spring.connectn.Services.Implementation;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Competence;
import tn.esprit.spring.connectn.Entities.Question;
import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleUpdateService {
    private final IRoleOngRepository roleRepository;
    private final ICompetenceRepository competenceRepository;
    private final ITestRepository testRepository;
    private final IQuestionRepository questionRepository;
    private final IOptionReponseRepository optionReponseRepository;

    @Transactional
    public RoleOng updateRoleWithDependencies(Long roleId, RoleOng roleUpdate, List<Competence> competences,
                                              List<Test> tests) {
        // 1. Mettre à jour le rôle de base
        RoleOng existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        existingRole.setTitle(roleUpdate.getTitle());
        existingRole.setDescription(roleUpdate.getDescription());
        existingRole.setDisponibiliteRequise(roleUpdate.getDisponibiliteRequise());

        // 2. Mettre à jour les compétences
        updateCompetences(existingRole, competences);

        // 3. Mettre à jour les tests et leurs dépendances
        updateTests(existingRole, tests);

        return roleRepository.save(existingRole);
    }

    private void updateCompetences(RoleOng role, List<Competence> competences) {
        // Supprimer les associations existantes
        role.getCompetencesRequises().clear();

        // Ajouter les nouvelles compétences
        competences.forEach(competence -> {
            Competence existingCompetence = competenceRepository.findById(competence.getIdCompetence())
                    .orElseThrow(() -> new EntityNotFoundException("Competence not found"));
            role.getCompetencesRequises().add(existingCompetence);
        });
    }

    private void updateTests(RoleOng role, List<Test> tests) {
        // Supprimer les tests existants non présents dans la nouvelle liste
        List<Test> existingTests = testRepository.findByRoleId(role.getIdRoleOng());
        existingTests.forEach(existingTest -> {
            if (tests.stream().noneMatch(t -> t.getIdTest().equals(existingTest.getIdTest()))) {
                deleteTestDependencies(existingTest);
                testRepository.delete(existingTest);
            }
        });

        // Mettre à jour ou créer les tests
        tests.forEach(test -> {
            if (test.getIdTest() != null) {
                // Mise à jour du test existant
                Test existingTest = testRepository.findById(test.getIdTest())
                        .orElseThrow(() -> new EntityNotFoundException("Test not found"));
                existingTest.setName(test.getName());
                updateQuestions(existingTest, test.getQuestions());
                testRepository.save(existingTest);
            } else {
                // Création d'un nouveau test
                test.setRoleOng(role);
                test.getQuestions().forEach(this::saveQuestionWithOptions);
                testRepository.save(test);
            }
        });
    }

    private void updateQuestions(Test test, List<Question> questions) {
        // Logique similaire à updateTests pour les questions
        // ...
    }

    private void saveQuestionWithOptions(Question question) {
        question.getOptionReponses().forEach(optionReponseRepository::save);
        questionRepository.save(question);
    }

    private void deleteTestDependencies(Test test) {
        test.getQuestions().forEach(question -> {
            optionReponseRepository.deleteByQuestionId(question.getIdQuestion());
            questionRepository.delete(question);
        });
        // Supprimer autres dépendances si nécessaire
    }
}