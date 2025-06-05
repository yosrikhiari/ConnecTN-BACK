package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.RoleRecommande;
import tn.esprit.spring.connectn.Services.Implementation.RoleRecommandeService;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/roleRecommande")
public class RoleRecommandeController {

    @Autowired
    private RoleRecommandeService roleRecommandeService;

    @PostMapping("/add")
    public RoleRecommande addRoleRecommande(@RequestBody RoleRecommande roleRecommande) {
        return roleRecommandeService.addRoleRecommande(roleRecommande);
    }

    @GetMapping("/getAll")
    public List<RoleRecommande> getAllRoleRecommande() {
        return roleRecommandeService.getAllRoleRecommandes();
    }

    @GetMapping("/get/{id}")
    public Optional<RoleRecommande> getRoleRecommande(@PathVariable Long id) {
        return roleRecommandeService.getRoleRecommandeById(id);
    }


    @PutMapping("/edit/{id}")
    public RoleRecommande updarRoleRecommande(@PathVariable Long id,@RequestBody RoleRecommande roleRecommande) {
        return roleRecommandeService.updateRoleRecommande(id, roleRecommande);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRoleRecommande(@PathVariable Long id) {
        roleRecommandeService.deleteRoleRecommande(id);
    }
}