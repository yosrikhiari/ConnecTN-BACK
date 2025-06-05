package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.Builder;
import lombok.Data;
import tn.esprit.spring.connectn.Entities.ApplicationStatus;
import tn.esprit.spring.connectn.Entities.ShopApplication;

import java.time.LocalDateTime;

@Data
@Builder
public class ShopApplicationStatusDTO {
    private ApplicationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewNotes;

    public static ShopApplicationStatusDTO fromEntity(ShopApplication application) {
        return ShopApplicationStatusDTO.builder()
                .status(application.getStatus())
                .submittedAt(application.getSubmittedAt())
                .reviewedAt(application.getReviewedAt())
                .reviewNotes(application.getReviewNotes())
                .build();
    }
}