package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;


import tn.esprit.spring.connectn.Entities.DocumentVerification;

import java.time.LocalDateTime;

public record DocumentVerificationResponse(
        Long id,
        String documentType,
        String fileName,
        boolean verified,
        LocalDateTime uploadedAt
) {
    public static DocumentVerificationResponse fromEntity(DocumentVerification doc) {
        return new DocumentVerificationResponse(
                doc.getId(),
                doc.getDocumentType(),
                doc.getFileName(),
                doc.isVerified(),
                doc.getUploadedAt()
        );
    }
}
