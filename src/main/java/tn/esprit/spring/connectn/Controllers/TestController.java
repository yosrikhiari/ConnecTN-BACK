package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Test;
import tn.esprit.spring.connectn.Services.Implementation.TestService;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/add")
    public Test addTest(@RequestBody Test test) {
        return testService.addTest(test);
    }

    @GetMapping("/getAll")
    public List<Test> getAllTests() {
        return testService.getAllTests();
    }

    @GetMapping("/get/{id}")
    public Optional<Test> getTest(@PathVariable Long id) {
        return testService.getTestById(id);
    }

    @PutMapping("/edit/{id}")
    public Test editTest(@PathVariable Long id, @RequestBody Test test) {
        return testService.updateTest(id, test);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
    }
}