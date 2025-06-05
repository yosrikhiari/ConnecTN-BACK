package tn.esprit.spring.connectn.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.ShopReview;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Long> {

    // Find all reviews for a specific shop with pagination
    Page<ShopReview> findByShopIdOrderByCreatedAtDesc(Long shopId, Pageable pageable);

    // Find a review by user and shop
    Optional<ShopReview> findByUserIdAndShopId(Long userId, Long shopId);

    // Find all reviews by a specific user
    List<ShopReview> findByUserId(Long userId);

    // Count reviews by shop
    Long countByShopId(Long shopId);

    // Calculate average rating for a shop
    @Query("SELECT AVG(r.rating) FROM ShopReview r WHERE r.shop.id = :shopId")
    Float averageRatingByShopId(@Param("shopId") Long shopId);

    // Count reviews by shop and rating
    @Query("SELECT r.rating, COUNT(r) FROM ShopReview r WHERE r.shop.id = :shopId GROUP BY r.rating")
    List<Object[]> countReviewsByRating(@Param("shopId") Long shopId);
}