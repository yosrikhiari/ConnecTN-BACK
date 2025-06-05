package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.RoleRecommande;

import java.util.List;
import java.util.Optional;

public interface IRoleRecommandeService {
    List<RoleRecommande> getAllRoleRecommandes();
    RoleRecommande addRoleRecommande(RoleRecommande roleRecommande);
    Optional<RoleRecommande> getRoleRecommandeById(long id);
    RoleRecommande updateRoleRecommande(Long id,RoleRecommande roleRecommande);
    void deleteRoleRecommande(Long id);
}