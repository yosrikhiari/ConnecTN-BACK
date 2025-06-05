package tn.esprit.spring.connectn.Services.Implementation;

import com.cloudinary.Cloudinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.OrganisationNG;
import tn.esprit.spring.connectn.Repository.IOrganisationNGRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IOrganisationNGService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrganisationNGService implements IOrganisationNGService {

    @Autowired
    private IOrganisationNGRepository organisationNGRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public OrganisationNG addOrganisation(OrganisationNG organisation, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), Map.of());
            String imageUrl = (String) uploadResult.get("url");
            organisation.setImageUrl(imageUrl);
        }
        return organisationNGRepository.save(organisation);
    }

    @Override
    public List<OrganisationNG> getAllOrganisations() {
        return organisationNGRepository.findAll();
    }

    @Override
    public Optional<OrganisationNG> findOrganisationById(Long organisationId) {
        return organisationNGRepository.findById(organisationId);
    }

    @Override
    public OrganisationNG updateOrganisation(Long id, OrganisationNG organisation, MultipartFile imageFile) throws IOException {
        OrganisationNG ong = organisationNGRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No such organisation"));

        ong.setName(organisation.getName());
        ong.setDescription(organisation.getDescription());
        ong.setMission(organisation.getMission());
        ong.setRegion(organisation.getRegion());
        ong.setDomaineAction(organisation.getDomaineAction());
        ong.setTaille(organisation.getTaille());
        ong.setContact(organisation.getContact());

        if (imageFile != null && !imageFile.isEmpty()) {
            // Supprimer l'ancienne image si elle existe
            if (ong.getImageUrl() != null) {
                // Extraire l'ID public de l'URL ou le stocker séparément
                // cloudinary.uploader().destroy(publicId, Map.of());
            }

            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), Map.of());
            String imageUrl = (String) uploadResult.get("url");
            ong.setImageUrl(imageUrl);
        }

        return organisationNGRepository.save(ong);
    }

    @Override
    public void deleteOrganisation(Long organisationId) {
        OrganisationNG ong = organisationNGRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("No such organisation"));

        // Supprimer l'image de Cloudinary si elle existe
        if (ong.getImageUrl() != null) {
            // Extraire l'ID public de l'URL ou le stocker séparément
            // cloudinary.uploader().destroy(publicId, Map.of());
        }

        organisationNGRepository.delete(ong);
    }
}