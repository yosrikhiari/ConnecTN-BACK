package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.DocumentVerification;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVerificationRepository extends JpaRepository<DocumentVerification, Long> {

    List<DocumentVerification> findByShopApplicationId(Long shopApplicationId);

    Optional<DocumentVerification> findById(Long id);
}
