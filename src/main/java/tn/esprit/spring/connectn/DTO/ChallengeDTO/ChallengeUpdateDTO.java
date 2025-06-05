package tn.esprit.spring.connectn.DTO.ChallengeDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.ChallengeStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeUpdateDTO {
    private ChallengeStatus status;
}