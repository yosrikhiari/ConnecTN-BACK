package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.Membre;
import tn.esprit.spring.connectn.Services.Implementation.MembreService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/membre")
public class MembreController {

    @Autowired
    private MembreService membreService;

    @PostMapping("/add/{organisationId}/{roleId}/{userId}/{temoignage}")
    public Membre addMembreByIds(@PathVariable Long organisationId, @PathVariable Long roleId, @PathVariable Long userId, @PathVariable String temoignage) {
        return membreService.addMembreByIds(organisationId, roleId, userId,temoignage);
    }

    @PostMapping("/add")
    public void addMembre(@RequestBody Membre membre) {
        membreService.addMembre(membre);
    }

    @GetMapping("/getAll")
    public List<Membre> getAll() {
        return membreService.getAllMembres();
    }

    @GetMapping("/get/{id}")
    public Optional<Membre> getMembre(@PathVariable Long id) {
        return membreService.findMembreById(id);
    }

    @PutMapping("/edit/{id}")
    public Membre updateMembre(@PathVariable Long id, @RequestBody Membre membre) {
        return membreService.updateMembre(id,membre);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMembre(@PathVariable Long id) {
        membreService.deleteMembre(id);
    }
}