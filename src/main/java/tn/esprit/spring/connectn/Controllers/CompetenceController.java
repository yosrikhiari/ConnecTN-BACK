package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Competence;
import tn.esprit.spring.connectn.Services.Implementation.CompetenceService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/competence")
public class CompetenceController {

    @Autowired
    private CompetenceService competenceService;

    @PostMapping("/add/{idUser}")
    public Competence addCompetence(@RequestBody Competence competence, @PathVariable Long idUser) {
        return competenceService.addCompetence(competence,idUser);
    }

    @PostMapping("/add")
    public Competence addCompetenceOng(@RequestBody Competence competence) {
        return competenceService.addCompetenceOng(competence);
    }

    @GetMapping("/getAll")
    public List<Competence> getAllCompetences() {
        return competenceService.getAllCompetences();
    }

    @GetMapping("/get/{id}")
    public Optional<Competence> getCompetence(@PathVariable Long id) {
        return competenceService.findCompetenceById(id);
    }

    @PutMapping("/edit/{id}")
    public Competence editCompetence(@PathVariable Long id, @RequestBody Competence competence) {
        return competenceService.updateCompetence(id, competence);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCompetence(@PathVariable Long id) {
        competenceService.deleteCompetence(id);
    }
}