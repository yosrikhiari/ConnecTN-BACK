package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeRequestDTO;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeResponseDTO;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeUpdateDTO;

import java.util.List;
public interface IChallengeService {
    ChallengeResponseDTO createChallenge(ChallengeRequestDTO request);
    List<ChallengeResponseDTO> getAllChallenges();
    ChallengeResponseDTO getChallengeById(Long id);
    void deleteChallenge(Long id);
    int getParticipantsCount(Long id);
    void participateInChallenge(Long challengeId, Long userId, int pointsToAdd);
    void removeParticipationFromChallenge(Long challengeId, Long userId, int pointsToRemove);
    boolean isUserParticipated(Long challengeId, Long userId);
    boolean updateChallengeStatus(Long challengeId, ChallengeUpdateDTO updateDTO);
}