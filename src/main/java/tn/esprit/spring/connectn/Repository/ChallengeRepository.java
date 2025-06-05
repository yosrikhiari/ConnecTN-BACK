package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.Challenge;
import tn.esprit.spring.connectn.Entities.ChallengeStatus;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByStatus(ChallengeStatus status);
    Challenge findByid(Long id);
    List<Challenge> findByUser_Id(Long adminId);
    List<Challenge> findAllByOrderByCreatedAtDesc();
    @Query("SELECT SIZE(c.participants) FROM Challenge c WHERE c.id = :challengeId")
    int countParticipantsBychallengeId(Long challengeId);
}