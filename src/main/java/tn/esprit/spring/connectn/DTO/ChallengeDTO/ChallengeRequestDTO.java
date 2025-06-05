package tn.esprit.spring.connectn.DTO.ChallengeDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.ChallengeStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeRequestDTO {
    private String title;
    private String description;
    private String image;
    private int points;
    private Long userId;
}