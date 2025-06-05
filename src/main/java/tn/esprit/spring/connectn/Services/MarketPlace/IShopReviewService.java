package tn.esprit.spring.connectn.Services.MarketPlace;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.CreateReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewSummaryDto;

import java.util.List;

public interface IShopReviewService {

    /**
     * Get paginated reviews for a shop
     */
    Page<ReviewDto> getShopReviews(Long shopId, Pageable pageable);

    /**
     * Create a new review
     */
    ReviewDto createReview(CreateReviewDto reviewDto, Long userId);

    /**
     * Get a specific review by id
     */
    ReviewDto getReviewById(Long id);

    /**
     * Update an existing review
     */
    ReviewDto updateReview(Long id, CreateReviewDto reviewDto, Long userId);

    /**
     * Delete a review
     */
    void deleteReview(Long id, Long userId);

    /**
     * Get all reviews by a user
     */
    List<ReviewDto> getUserReviews(Long userId);

    /**
     * Get summary statistics for shop reviews
     */
    ReviewSummaryDto getReviewSummary(Long shopId);

    /**
     * Get a user's review for a specific shop if it exists
     */
    ReviewDto getUserShopReview(Long userId, Long shopId);
}