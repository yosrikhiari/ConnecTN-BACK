package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.RoleOng;

import java.util.List;
import java.util.Optional;

public interface IRoleOngService {

    List<RoleOng> getAllRoles();
    RoleOng addRole(RoleOng role);
    Optional<RoleOng> getRoleById(long id);
    RoleOng updateRole(Long id, RoleOng role);
    void deleteRole(Long id);
}