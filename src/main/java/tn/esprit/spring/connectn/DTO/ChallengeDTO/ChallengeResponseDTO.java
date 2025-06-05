package tn.esprit.spring.connectn.DTO.ChallengeDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.ChallengeStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String image;
    private int points;
    private int nbVotes;
    private LocalDateTime createdAt;
    private ChallengeStatus status;
    private Long userId;
    private List<Long> participantIds;
}