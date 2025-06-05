package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.Builder;
import lombok.Data;
import tn.esprit.spring.connectn.Entities.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ShopApplicationResponse {
    private Long id;
    private UserBasicDTO user;
    private String certification;
    private ApplicationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private List<DocumentVerificationDTO> documentVerifications;

    public static ShopApplicationResponse fromEntity(ShopApplication application) {
        return ShopApplicationResponse.builder()
                .id(application.getId())
                .user(UserBasicDTO.fromEntity(application.getUser()))
                .certification(application.getCertification())
                .status(application.getStatus())
                .submittedAt(application.getSubmittedAt())
                .reviewedAt(application.getReviewedAt())
                .reviewNotes(application.getReviewNotes())
                .documentVerifications(application.getDocumentVerifications().stream()
                        .map(DocumentVerificationDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    @Data
    @Builder
    public static class UserBasicDTO {
        private Long id;
        private String username;
        private String emailAddress;
        private Role role;

        public static UserBasicDTO fromEntity(User user) {
            return UserBasicDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .emailAddress(user.getEmailAddress())
                    .role(user.getRole())
                    .build();
        }
    }

    @Data
    @Builder
    public static class DocumentVerificationDTO {
        private Long id;
        private String documentType;
        private String fileName;
        private boolean verified;
        private String verificationNotes;

        public static DocumentVerificationDTO fromEntity(DocumentVerification doc) {
            return DocumentVerificationDTO.builder()
                    .id(doc.getId())
                    .documentType(doc.getDocumentType())
                    .fileName(doc.getFileName())
                    .verified(doc.isVerified())
                    .verificationNotes(doc.getVerificationNotes())
                    .build();
        }
    }
}