package tn.esprit.spring.connectn.Services.MarketPlace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.PythonVerificationResponse;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.VerificationResult;
import tn.esprit.spring.connectn.Entities.DocumentVerification;
import tn.esprit.spring.connectn.Entities.ShopApplication;
import tn.esprit.spring.connectn.Repository.DocumentVerificationRepository;
import tn.esprit.spring.connectn.Repository.ShopApplicationRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {
    private final DocumentVerificationRepository documentVerificationRepository;
    private final ShopApplicationRepository shopApplicationRepository;
    private final PythonVerificationService pythonVerificationService;

    @Transactional
    public DocumentVerification addDocumentToApplication(Long applicationId, MultipartFile file, String documentType) throws IOException {
        ShopApplication application = shopApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Shop application not found"));

        DocumentVerification verification = new DocumentVerification();
        verification.setDocumentType(documentType);
        verification.setFileName(file.getOriginalFilename());
        verification.setFileContentType(file.getContentType());
        verification.setFileData(file.getBytes());
        verification.setVerified(false);

        application.addDocumentVerification(verification);
        return documentVerificationRepository.save(verification);
    }

    @Transactional
    public VerificationResult verifyDocument(Long verificationId) {
        DocumentVerification verification = documentVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        try {
            PythonVerificationResponse response = pythonVerificationService.verifyDocument(
                    verification.getFileData(),
                    verification.getFileName()
            );

            if (response != null && !response.getDetections().isEmpty()) {
                String notes = "Verified certifications: " + response.getDetections().stream()
                        .map(d -> d.getClass_name() + " (" + d.getConfidence() + ")")
                        .collect(Collectors.joining(", "));
                verification.verify(notes);
                documentVerificationRepository.save(verification);
                return new VerificationResult(true, verification, null);
            } else {
                verification.reject("No valid certifications detected");
                documentVerificationRepository.save(verification);
                return new VerificationResult(false, verification, "No valid certifications detected");
            }
        } catch (Exception e) {
            verification.reject("Verification failed: " + e.getMessage());
            documentVerificationRepository.save(verification);
            return new VerificationResult(false, verification, e.getMessage());
        }
    }

    @Transactional
    public List<DocumentVerification> verifyAllDocumentsForApplication(Long applicationId) {
        ShopApplication application = shopApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        List<DocumentVerification> verifiedDocs = application.getDocumentVerifications().stream()
                .map(doc -> verifyDocument(doc.getId()).getDocument())
                .collect(Collectors.toList());

        return verifiedDocs;
    }

    public List<DocumentVerification> getAllVerificationsForApplication(Long applicationId) {
        return documentVerificationRepository.findByShopApplicationId(applicationId);
    }

    @Transactional
    public DocumentVerification updateVerificationStatus(Long verificationId, boolean verified, String notes) {
        DocumentVerification verification = documentVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (verified) {
            verification.verify(notes);
        } else {
            verification.reject(notes);
        }
        return documentVerificationRepository.save(verification);
    }
}