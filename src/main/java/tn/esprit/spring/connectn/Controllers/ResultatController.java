package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Resultat;
import tn.esprit.spring.connectn.Services.Implementation.ResultatService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/resultat")
public class ResultatController {

    @Autowired
    private ResultatService resultatService;

    @PostMapping("/add/{score}/{idUser}/{idTest}")
    public Resultat addResultatWithParams(@PathVariable Double score, @PathVariable Long idUser, @PathVariable Long idTest) {
        return resultatService.addResultatFull(score, idUser, idTest);
    }
    @PostMapping("/add")
    public Resultat addResultat(@RequestBody Resultat resultat) {
        return resultatService.addResultat(resultat);
    }

    @GetMapping("/getAll")
    public List<Resultat> getAllResultats() {
        return resultatService.getAllResultats();
    }

    @GetMapping("/get/{id}")
    public Optional<Resultat> getResultat(@PathVariable Long id) {
        return resultatService.getResultatById(id);
    }

    @PutMapping("/edit/{id}")
    public Resultat editResultat(@PathVariable Long id, @RequestBody Resultat resultat) {
        return resultatService.updateResultat(id, resultat);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteResultat(@PathVariable Long id) {
        resultatService.deleteResultat(id);
    }

}