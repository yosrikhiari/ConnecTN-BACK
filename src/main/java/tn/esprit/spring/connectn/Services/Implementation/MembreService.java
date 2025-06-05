package tn.esprit.spring.connectn.Services.Implementation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.Membre;
import tn.esprit.spring.connectn.Entities.OrganisationNG;
import tn.esprit.spring.connectn.Entities.RoleOng;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Repository.IMembreRepository;
import tn.esprit.spring.connectn.Repository.IOrganisationNGRepository;
import tn.esprit.spring.connectn.Repository.IRoleOngRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IMembreService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MembreService implements IMembreService {

    @Autowired
    private IMembreRepository membreRepository;

    @Autowired
    private IOrganisationNGRepository organisationNGRepository;

    @Autowired
    private IRoleOngRepository roleRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<Membre> getAllMembres() {
        return membreRepository.findAll() ;
    }

    @Override
    public Membre addMembre(Membre membre) {
        return membreRepository.save(membre);
    }

    @Override
    public Membre addMembreByIds(Long idOrganisationNG, Long idRole, Long idUser,String temoignage) {
        OrganisationNG organisation = organisationNGRepository.findById(idOrganisationNG)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        RoleOng role = roleRepository.findById(idRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membre membre = Membre.builder()
                .organisationNG(organisation)
                .roleOng(role)
                .user(user)
                .temoignage(temoignage)
                .dateAdded(new Date())
                .build();

        return membreRepository.save(membre);
    }

    @Override
    public Optional<Membre> findMembreById(Long membreId) {
        return membreRepository.findById(membreId);
    }

    @Override
    public Membre updateMembre(Long id, Membre membre) {
        Membre membre1=membreRepository.findById(id).orElseThrow(()->new RuntimeException("No such membre"));
        membre1.setUser(membre.getUser());
        membre1.setRoleOng(membre.getRoleOng());
        membre1.setOrganisationNG(membre.getOrganisationNG());
        membre1.setDateAdded(membre.getDateAdded());
        membre1.setTemoignage(membre.getTemoignage());
        return membreRepository.save(membre1);
    }

    @Override
    public void deleteMembre(Long id) {
        Membre membre1=membreRepository.findById(id).orElseThrow(()->new RuntimeException("No such membre"));
        membreRepository.delete(membre1);
    }
}