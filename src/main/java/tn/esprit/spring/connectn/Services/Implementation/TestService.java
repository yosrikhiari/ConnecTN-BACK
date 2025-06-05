package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Repository.IRoleOngRepository;
import tn.esprit.spring.connectn.Repository.ITestRepository;
import tn.esprit.spring.connectn.Services.Interfaces.ITestService;

import java.util.List;
import java.util.Optional;

@Service
public class TestService implements ITestService {

    @Autowired
    private ITestRepository testRepository;
    @Autowired
    private IRoleOngRepository roleRepository;

    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @Override
    public Test addTest(Test test) {
        if (test.getRoleOng() != null && test.getRoleOng().getIdRoleOng() != null) {
            RoleOng role = roleRepository.findById(test.getRoleOng().getIdRoleOng())
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
            test.setRoleOng(role);
        }
        return testRepository.save(test);
    }

    @Override
    public Optional<Test> getTestById(long id) {
        return testRepository.findById(id);
    }

    @Override
    public Test updateTest(Long id, Test test) {
        Test test1 = testRepository.findById(id).orElseThrow(()-> new RuntimeException("Test not found"));
        test1.setQuestions(test.getQuestions());
        return testRepository.save(test1);
    }

    @Override
    public void deleteTest(Long id) {
        Test test1 = testRepository.findById(id).orElseThrow(()-> new RuntimeException("Test not found"));
        testRepository.delete(test1);

    }
}