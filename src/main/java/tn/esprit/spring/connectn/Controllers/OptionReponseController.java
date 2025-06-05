package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.OptionReponse;
import tn.esprit.spring.connectn.Services.Implementation.OptionReponseService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/optionReponse")
public class OptionReponseController {

    @Autowired
    private OptionReponseService optionReponseService;

    @PostMapping("/add/{idQuestion}/{text}/{valeur}")
    public OptionReponse addOptionReponse(
            @PathVariable Long idQuestion,
            @PathVariable String text,
            @PathVariable Long valeur) {

        return optionReponseService.addOptionReponseFull(idQuestion, text, valeur);
    }

    @PostMapping("/add")
    public void addOptionReponse(@RequestBody OptionReponse optionReponse) {
        optionReponseService.addOptionReponse(optionReponse);
    }

    @GetMapping("/getAll")
    public List<OptionReponse> getAllOptionReponses() {
        return optionReponseService.getAllOptionReponses();
    }

    @GetMapping("/get/{id}")
    public Optional<OptionReponse> getOptionReponse(@PathVariable Long id) {
        return optionReponseService.findOptionReponseById(id);
    }


    @PutMapping("/edit/{id}")
    public OptionReponse updateOptionReponse(@RequestBody OptionReponse optionReponse, @PathVariable Long id) {
        return optionReponseService.updateOptionReponse(id,optionReponse);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOptionReponse(@PathVariable Long id) {
        optionReponseService.deleteOptionReponse(id);
    }
}