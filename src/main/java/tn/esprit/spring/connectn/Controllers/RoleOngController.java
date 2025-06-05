package tn.esprit.spring.connectn.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Entities.RoleUpdateRequest;
import tn.esprit.spring.connectn.Services.Implementation.RoleCascadeService;
import tn.esprit.spring.connectn.Services.Implementation.RoleOngService;
import tn.esprit.spring.connectn.Services.Implementation.RoleUpdateService;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/role")
public class RoleOngController {

    @Autowired
    private RoleOngService roleService;

    @Autowired
    private RoleCascadeService roleCascadeService;

    @Autowired
    private RoleUpdateService roleUpdateService;

    @PostMapping("/add")
    public RoleOng addRole(@RequestBody RoleOng role) {
        return roleService.addRole(role);
    }

    @GetMapping("/getAll")
    public List<RoleOng> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/get/{id}")
    public Optional<RoleOng> getRole(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PutMapping("/edit/{id}")
    public RoleOng editRole(@PathVariable Long id, @RequestBody RoleOng role) {
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoleWithCascade(@PathVariable Long id) {
        try {
            roleCascadeService.deleteRoleWithDependencies(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/update-with-dependencies/{id}")
    public ResponseEntity<RoleOng> updateRoleWithDependencies(
            @PathVariable Long id,
            @RequestBody RoleUpdateRequest request) {

        RoleOng updatedRole = roleUpdateService.updateRoleWithDependencies(
                id,
                request.getRoleOng(),
                request.getCompetences(),
                request.getTests()
        );

        return ResponseEntity.ok(updatedRole);
    }


}