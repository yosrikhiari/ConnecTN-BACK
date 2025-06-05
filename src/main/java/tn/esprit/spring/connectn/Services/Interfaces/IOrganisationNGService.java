package tn.esprit.spring.connectn.Services.Interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.OrganisationNG;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IOrganisationNGService {
    OrganisationNG addOrganisation(OrganisationNG organisation, MultipartFile imageFile) throws IOException;
    List<OrganisationNG> getAllOrganisations();
    Optional<OrganisationNG> findOrganisationById(Long organisationId);
    OrganisationNG updateOrganisation(Long id, OrganisationNG organisation, MultipartFile imageFile) throws IOException;
    void deleteOrganisation(Long organisationId);
}