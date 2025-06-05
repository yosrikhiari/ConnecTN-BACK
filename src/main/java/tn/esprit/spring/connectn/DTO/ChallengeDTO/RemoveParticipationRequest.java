package tn.esprit.spring.connectn.DTO.ChallengeDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveParticipationRequest {
    private Long userId;
    private int pointsToRemove;
}