package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.PromoCode;
import tn.esprit.spring.connectn.Entities.Shop;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCode(String code);
    List<PromoCode> findByUserIdAndShopId(Long userId, Long shopId);
    List<PromoCode> findByShopId(Long shopId);

    List<PromoCode> findByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);
    boolean existsByUserIdAndShopIdAndExpiresAtAfter(Long userId, Long shopId, LocalDateTime now);
}