package tn.esprit.spring.connectn.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.ChallengeDTO.*;
import tn.esprit.spring.connectn.Services.Interfaces.IChallengeService;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class ChallengeController {

    private final IChallengeService challengeService;
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<ChallengeResponseDTO> create(@RequestBody ChallengeRequestDTO request) {
        return ResponseEntity.ok(challengeService.createChallenge(request));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<ChallengeResponseDTO>> getAll() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{challengeId}/participants-count")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable Long challengeId) {
        try {
            // Call service layer to get participant count
            int participantCount = challengeService.getParticipantsCount(challengeId);
            return ResponseEntity.ok(participantCount);
        } catch (Exception e) {
            // Handle any errors (challenge not found, database issues, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);  // You can replace with an appropriate error message
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/{challengeId}/participate")
    public ResponseEntity<String> participate(
            @PathVariable Long challengeId,
            @RequestBody ParticipationRequestDTO request
    ) {
        challengeService.participateInChallenge(challengeId, request.getUserId(), request.getPointsToAdd());
        return ResponseEntity.ok("Participation successful and points added");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{challengeId}/remove-participation")
    public ResponseEntity<String> removeParticipation(
            @PathVariable Long challengeId,
            @RequestBody RemoveParticipationRequest request) {
        try {
            challengeService.removeParticipationFromChallenge(challengeId, request.getUserId(), request.getPointsToRemove());
            return ResponseEntity.ok("User removed from challenge and points deducted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove participation: " + e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/{challengeId}/is-participated")
    public ResponseEntity<Boolean> isUserParticipated(
            @PathVariable Long challengeId,
            @RequestBody ParticipationCheckDTO dto) {
        boolean participated = challengeService.isUserParticipated(challengeId, dto.getUserId());
        return ResponseEntity.ok(participated);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/{challengeId}/status")
    public ResponseEntity<String> updateChallengeStatus(
            @PathVariable Long challengeId,
            @RequestBody ChallengeUpdateDTO updateDTO) {
        boolean updated = challengeService.updateChallengeStatus(challengeId, updateDTO);
        if (updated) {
            return ResponseEntity.ok("Challenge status updated successfully");
        } else {
            return ResponseEntity.status(404).body("Challenge not found");
        }
    }

}