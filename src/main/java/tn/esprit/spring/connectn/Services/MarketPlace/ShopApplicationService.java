package tn.esprit.spring.connectn.Services.MarketPlace;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.*;
import tn.esprit.spring.connectn.Repository.ShopApplicationRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopApplicationService {
    private final ShopApplicationRepository shopApplicationRepository;
    private final DocumentVerificationService documentVerificationService;

    private final UserRepository userRepository;
    public ShopApplicationService(ShopApplicationRepository shopApplicationRepository,
                                  DocumentVerificationService documentVerificationService,
                                  UserRepository userRepository) {
        this.shopApplicationRepository = shopApplicationRepository;
        this.documentVerificationService = documentVerificationService;
        this.userRepository = userRepository;
    }

    public List<ShopApplication> getApplicationsByStatus(ApplicationStatus status) {
        return shopApplicationRepository.findByStatus(status);
    }

    @Transactional
    public ShopApplication createApplication(User user, String certification,
                                             String documentType, MultipartFile[] documents) {
        try {
            ShopApplication application = new ShopApplication();
            application.setUser(user); // Set the user object instead of just ID
            application.setCertification(certification);
            application.setStatus(ApplicationStatus.PENDING);

            shopApplicationRepository.save(application);

            if (documents != null) {
                for (MultipartFile document : documents) {
                    documentVerificationService.addDocumentToApplication(
                            application.getId(),
                            document,
                            documentType
                    );
                }
            }
            return application;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process documents", e);
        }
    }
    @Transactional
    public ShopApplication verifyAndProcessApplication(Long applicationId) {
        ShopApplication application = getApplicationById(applicationId);
        List<DocumentVerification> verifiedDocs =
                documentVerificationService.verifyAllDocumentsForApplication(applicationId);

        // After verification, check if all documents are verified
        if (application.areAllDocumentsVerified()) {
            return approveApplication(applicationId, "Auto-approved after verification");
        } else {
            return rejectApplication(applicationId, "Rejected due to verification failure");
        }
    }

    @Transactional
    public ShopApplication approveApplication(Long applicationId, String notes) {
        ShopApplication application = getApplicationById(applicationId);
        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewNotes(notes);
        User user = application.getUser();
        user.setRole(Role.SHOP_OWNER);
        userRepository.save(user);
        return shopApplicationRepository.save(application);
    }

    public List<ShopApplication> getAllApplications() {
        return shopApplicationRepository.findAll();
    }

    public ShopApplication getApplicationById(Long id) {
        return shopApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }

    @Transactional
    public ShopApplication updateApplicationStatus(Long applicationId, ApplicationStatus status, String notes) {
        ShopApplication application = getApplicationById(applicationId);
        application.setStatus(status);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewNotes(notes);
        return shopApplicationRepository.save(application);
    }

    @Transactional
    public ShopApplication markNeedsMoreInfo(Long applicationId, String notes) {
        return updateApplicationStatus(applicationId, ApplicationStatus.NEEDS_MORE_INFO, notes);
    }

    @Transactional
    public ShopApplication rejectApplication(Long applicationId, String notes) {
        return updateApplicationStatus(applicationId, ApplicationStatus.REJECTED, notes);
    }

    @Transactional
    public ShopApplication markUnderReview(Long applicationId) {
        return updateApplicationStatus(applicationId, ApplicationStatus.UNDER_REVIEW, null);
    }

    @Transactional
    public ShopApplication markAsRead(Long applicationId, String notes) {
        // First mark as under review
        ShopApplication application = markUnderReview(applicationId);

        // Then trigger the verification process
        return verifyAndProcessApplication(applicationId);
    }

    @Transactional
    public void deleteApplication(Long id) {
        ShopApplication application = getApplicationById(id);
        if (application.getStatus() == ApplicationStatus.APPROVED ||
                application.getStatus() == ApplicationStatus.REJECTED) {
            shopApplicationRepository.delete(application);
        } else {
            throw new IllegalStateException("Cannot delete application in current state");
        }
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void processPendingVerifications() {
        shopApplicationRepository.findByStatus(ApplicationStatus.PENDING)
                .forEach(app -> documentVerificationService.verifyAllDocumentsForApplication(app.getId()));
    }
    public List<ShopApplication> getApplicationsByUserId(Long userId) {
        // Verify user exists first
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return shopApplicationRepository.findByUser_Id(userId);
    }

}