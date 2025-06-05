package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Repository.IRoleOngRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IRoleOngService;

import java.util.List;
import java.util.Optional;

@Service
public class RoleOngService implements IRoleOngService {

    @Autowired
    private IRoleOngRepository roleRepository;
    @Override
    public List<RoleOng> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleOng addRole(RoleOng role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<RoleOng> getRoleById(long id) {
        return roleRepository.findById(id);
    }

    @Override
    public RoleOng updateRole(Long id, RoleOng role) {
        RoleOng role1 = roleRepository.findById(id).orElseThrow(()-> new RuntimeException("Role not found"));
        role1.setTitle(role.getTitle());
        role1.setDescription(role.getDescription());
        role1.setCompetencesRequises(role.getCompetencesRequises());
        role1.setOrganisationNG(role.getOrganisationNG());
        role1.setDisponibiliteRequise(role.getDisponibiliteRequise());
        return roleRepository.save(role1);
    }

    @Override
    public void deleteRole(Long id) {
        RoleOng role1 = roleRepository.findById(id).orElseThrow(()-> new RuntimeException("Role not found"));
        roleRepository.delete(role1);

    }
}