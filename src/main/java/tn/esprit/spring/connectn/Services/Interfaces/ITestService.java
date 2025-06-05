package tn.esprit.spring.connectn.Services.Interfaces;



import tn.esprit.spring.connectn.Entities.Test;

import java.util.List;
import java.util.Optional;

public interface ITestService {

    List<Test> getAllTests();
    Test addTest(Test test);
    Optional<Test> getTestById(long id);
    Test updateTest(Long id,Test test);
    void deleteTest(Long id);

}