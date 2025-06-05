package tn.esprit.spring.connectn.DTO.ChallengeDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDTO {
    private Long userId;
    private int pointsToAdd;
}