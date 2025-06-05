package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Reponse;
import tn.esprit.spring.connectn.Services.Implementation.ReponseService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reponse")

public class ReponseController {

    @Autowired
    private ReponseService reponseService;

    @PostMapping("/add/{idOptionReponse}/{idQuestion}/{idTest}/{idUser}")
    public Reponse addReponse(
            @PathVariable Long idOptionReponse,
            @PathVariable Long idQuestion,
            @PathVariable Long idTest,
            @PathVariable Long idUser) {

        return reponseService.addReponseFull(idOptionReponse, idQuestion, idTest, idUser);
    }

    @PostMapping("/add")
    public Reponse add(@RequestBody Reponse reponse) {
        return reponseService.addReponse(reponse);
    }

    @GetMapping("/getAll")
    public List<Reponse> getAll() {
        return reponseService.getAllReponses();
    }

    @GetMapping("/get/{id}")
    public Optional<Reponse> getReponse(@PathVariable Long id) {
        return reponseService.getReponse(id);
    }

    @PutMapping("/edit/{id}")
    public Reponse editReponse(@RequestBody Reponse reponse, @PathVariable Long id) {
        return reponseService.updateReponse(id, reponse);
    }


    @DeleteMapping("/delete/{id}")
    public void deleteReponse(@PathVariable Long id) {
        reponseService.deleteReponse(id);
    }
}