package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.OrganisationNG;
import tn.esprit.spring.connectn.Services.Implementation.OrganisationNGService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ong")
public class OrganisationNGController {

    @Autowired
    private OrganisationNGService organisationNGService;

    @PostMapping("/add")
    public OrganisationNG addOrganisation(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String mission,
            @RequestParam String region,
            @RequestParam String domaineAction,
            @RequestParam String taille,
            @RequestParam String contact,
            @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        OrganisationNG organisation = new OrganisationNG();
        organisation.setName(name);
        organisation.setDescription(description);
        organisation.setMission(mission);
        organisation.setRegion(region);
        organisation.setDomaineAction(domaineAction);
        organisation.setTaille(taille);
        organisation.setContact(contact);

        return organisationNGService.addOrganisation(organisation, imageFile);
    }

    @GetMapping("/getAll")
    public List<OrganisationNG> getAllOrganisations() {
        return organisationNGService.getAllOrganisations();
    }

    @GetMapping("/get/{id}")
    public Optional<OrganisationNG> getOrganisation(@PathVariable Long id) {
        return organisationNGService.findOrganisationById(id);
    }

    @PutMapping("/edit/{id}")
    public OrganisationNG updateOrganisation(
            @PathVariable Long id,
            @RequestPart OrganisationNG organisationNG,
            @RequestPart(required = false) MultipartFile imageFile) throws IOException {
        return organisationNGService.updateOrganisation(id, organisationNG, imageFile);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOrganisation(@PathVariable Long id) {
        organisationNGService.deleteOrganisation(id);
    }
}