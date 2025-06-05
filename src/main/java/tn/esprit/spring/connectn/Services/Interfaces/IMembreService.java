package tn.esprit.spring.connectn.Services.Interfaces;


import tn.esprit.spring.connectn.Entities.Membre;

import java.util.List;
import java.util.Optional;

public interface IMembreService {

    List<Membre> getAllMembres();
    Membre addMembre(Membre membre);
    Membre addMembreByIds(Long idOrganisationNG,Long idRole,Long idUser,String temoignage);
    Optional<Membre> findMembreById(Long membreId);
    Membre updateMembre(Long id,Membre membre);
    void deleteMembre(Long id);
}