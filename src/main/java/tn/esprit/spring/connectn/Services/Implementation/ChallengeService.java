package tn.esprit.spring.connectn.Services.Implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeUpdateDTO;
import tn.esprit.spring.connectn.Entities.Challenge;
import tn.esprit.spring.connectn.Entities.ChallengeStatus;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Services.Interfaces.IChallengeService;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeRequestDTO;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.ChallengeResponseDTO;
import tn.esprit.spring.connectn.Repository.ChallengeRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService implements IChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    @Override
    public ChallengeResponseDTO createChallenge(ChallengeRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage())
                .points(request.getPoints())
                .nbVotes(0)
                .status(ChallengeStatus.PENDING)
                .user(user)
                .build();

        Challenge saved = challengeRepository.save(challenge);
        return mapToDTO(saved);
    }

    @Override
    public int getParticipantsCount(Long challengeId) {
        return challengeRepository.countParticipantsBychallengeId(challengeId);
    }

    @Override
    public List<ChallengeResponseDTO> getAllChallenges() {
        return challengeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChallengeResponseDTO getChallengeById(Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        return mapToDTO(challenge);
    }

    @Override
    public void deleteChallenge(Long id) {
        challengeRepository.deleteById(id);
    }

    @Override
    public void participateInChallenge(Long challengeId, Long userId, int pointsToAdd) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!challenge.getParticipants().contains(user)) {
            challenge.getParticipants().add(user);

            int currentPoints = user.getPoints();
            user.setPoints(currentPoints + pointsToAdd);

            userRepository.save(user);
            challengeRepository.save(challenge);
        }
    }

    @Override
    public void removeParticipationFromChallenge(Long challengeId, Long userId, int pointsToRemove) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (challenge.getParticipants().contains(user)) {
            challenge.getParticipants().remove(user);

            int updatedPoints = Math.max(0, user.getPoints() - pointsToRemove);
            user.setPoints(updatedPoints);

            userRepository.save(user);
            challengeRepository.save(challenge);
        }
    }

    @Override
    public boolean isUserParticipated(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return challenge.getParticipants().contains(user);
    }

    @Override
    public boolean updateChallengeStatus(Long challengeId, ChallengeUpdateDTO updateDTO) {
        Challenge challenge = challengeRepository.findById(challengeId).orElse(null);
        if (challenge == null) {
            return false;
        }
        challenge.setStatus(updateDTO.getStatus());
        challengeRepository.save(challenge);
        return true;
    }

    private ChallengeResponseDTO mapToDTO(Challenge challenge) {
        return ChallengeResponseDTO.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .image(challenge.getImage())
                .points(challenge.getPoints())
                .nbVotes(challenge.getNbVotes())
                .createdAt(challenge.getCreatedAt())
                .status(challenge.getStatus())
                .userId(challenge.getUser() != null ? challenge.getUser().getId() : null)
                .participantIds(
                        challenge.getParticipants() != null
                                ? challenge.getParticipants().stream()
                                .map(User::getId)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }
}