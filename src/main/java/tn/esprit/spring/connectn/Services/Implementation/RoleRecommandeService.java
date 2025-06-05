package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.RoleRecommande;
import tn.esprit.spring.connectn.Repository.IRoleRecommandeRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IRoleRecommandeService;

import java.util.List;
import java.util.Optional;

@Service
public class RoleRecommandeService implements IRoleRecommandeService {

    @Autowired
    private IRoleRecommandeRepository roleRecommandeRepository;

    @Override
    public List<RoleRecommande> getAllRoleRecommandes() {
        return  roleRecommandeRepository.findAll();
    }

    @Override
    public RoleRecommande addRoleRecommande(RoleRecommande roleRecommande) {
        return roleRecommandeRepository.save(roleRecommande);
    }

    @Override
    public Optional<RoleRecommande> getRoleRecommandeById(long id) {
        return roleRecommandeRepository.findById(id);
    }

    @Override
    public RoleRecommande updateRoleRecommande(Long id, RoleRecommande roleRecommande) {
        RoleRecommande roleRecommande1 = roleRecommandeRepository.findById(id).orElseThrow(()-> new RuntimeException("RoleRecommande not found"));
        roleRecommande1.setRoleOng(roleRecommande.getRoleOng());
        roleRecommande1.setResultat(roleRecommande.getResultat());
        roleRecommande1.setScore(roleRecommande.getScore());
        roleRecommande1.setExplication(roleRecommande.getExplication());
        return roleRecommandeRepository.save(roleRecommande1);
    }

    @Override
    public void deleteRoleRecommande(Long id) {
        roleRecommandeRepository.deleteById(id);

    }
}