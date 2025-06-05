package tn.esprit.spring.connectn.Services.MarketPlace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.CreateReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewSummaryDto;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Entities.ShopReview;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Exceptions.UnauthorizedException;
import tn.esprit.spring.connectn.Repository.ShopRepository;
import tn.esprit.spring.connectn.Repository.ShopReviewRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopReviewService implements IShopReviewService {

    @Autowired
    private ShopReviewRepository reviewRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<ReviewDto> getShopReviews(Long shopId, Pageable pageable) {
        if (!shopRepository.existsById(shopId)) {
            throw new ResourceNotFoundException("Shop with id " + shopId + " not found");
        }

        Page<ShopReview> reviewsPage = reviewRepository.findByShopIdOrderByCreatedAtDesc(shopId, pageable);
        return reviewsPage.map(ReviewDto::fromEntity);
    }

    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewDto reviewDto, Long userId) {
        // Validate rating
        if (reviewDto.getRating() == null || reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Validate comment
        if (reviewDto.getComment() == null || reviewDto.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        // Check if shop exists
        Shop shop = shopRepository.findById(reviewDto.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + reviewDto.getShopId()));

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already reviewed this shop
        reviewRepository.findByUserIdAndShopId(userId, reviewDto.getShopId())
                .ifPresent(review -> {
                    throw new IllegalStateException("User already reviewed this shop");
                });

        // Create and save new review
        ShopReview review = ShopReview.builder()
                .shop(shop)
                .user(user)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        ShopReview savedReview = reviewRepository.save(review);

        // Update shop rating and review count
        updateShopStatistics(shop);

        return mapToDto(savedReview);
    }

    private void updateShopStatistics(Shop shop) {
        // Calculate new average rating
        Float averageRating = reviewRepository.averageRatingByShopId(shop.getId());
        shop.setRating(averageRating != null ? averageRating : 0.0f);

        // Update review count
        Long reviewCount = reviewRepository.countByShopId(shop.getId());
        shop.setReviews(reviewCount != null ? reviewCount.intValue() : 0);

        // Save the updated shop
        shopRepository.save(shop);
    }

    private ReviewDto mapToDto(ShopReview review) {
        return ReviewDto.builder()
                .id(review.getId())
                .shopId(review.getShop().getId())
                .userId(review.getUser().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
    @Override
    public ReviewDto getReviewById(Long id) {
        ShopReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        return ReviewDto.fromEntity(review);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long id, CreateReviewDto reviewDto, Long userId) {
        // Find the review
        ShopReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check if user is the author
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this review");
        }

        // Store old rating for shop rating recalculation
        Integer oldRating = review.getRating();

        // Update review
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        // Save review
        ShopReview updatedReview = reviewRepository.save(review);

        // Update shop rating
        Shop shop = review.getShop();
        if (shop.getReviews() > 0) {
            // Recalculate average
            float currentTotal = shop.getRating() * shop.getReviews();
            currentTotal = currentTotal - oldRating + reviewDto.getRating();
            shop.setRating(currentTotal / shop.getReviews());
            shopRepository.save(shop);
        }

        return ReviewDto.fromEntity(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, Long userId) {
        // Find the review
        ShopReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check if user is the author
        if (!review.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        // Update shop rating
        Shop shop = review.getShop();
        if (shop.getReviews() > 1) {
            float currentTotal = shop.getRating() * shop.getReviews();
            currentTotal -= review.getRating();
            shop.setReviews(shop.getReviews() - 1);
            shop.setRating(currentTotal / shop.getReviews());
        } else {
            // Last review is being deleted
            shop.setReviews(0);
            shop.setRating(0.0f);
        }
        shopRepository.save(shop);

        // Delete the review
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewDto> getUserReviews(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        List<ShopReview> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewSummaryDto getReviewSummary(Long shopId) {
        if (!shopRepository.existsById(shopId)) {
            throw new ResourceNotFoundException("Shop not found");
        }

        Float avgRating = reviewRepository.averageRatingByShopId(shopId);
        Long totalReviews = reviewRepository.countByShopId(shopId);

        List<Object[]> ratingCounts = reviewRepository.countReviewsByRating(shopId);
        Map<Integer, Integer> ratingBreakdown = new HashMap<>();

        // Initialize all ratings with 0
        for (int i = 1; i <= 5; i++) {
            ratingBreakdown.put(i, 0);
        }

        // Update with actual counts
        for (Object[] result : ratingCounts) {
            Integer rating = (Integer) result[0];
            Long count = (Long) result[1];
            ratingBreakdown.put(rating, count.intValue());
        }

        return ReviewSummaryDto.builder()
                .averageRating(avgRating != null ? avgRating : 0.0f)
                .totalReviews(totalReviews != null ? totalReviews.intValue() : 0)
                .ratingBreakdown(ratingBreakdown)
                .build();
    }

    @Override
    public ReviewDto getUserShopReview(Long userId, Long shopId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!shopRepository.existsById(shopId)) {
            throw new ResourceNotFoundException("Shop not found");
        }

        Optional<ShopReview> review = reviewRepository.findByUserIdAndShopId(userId, shopId);
        return review.map(ReviewDto::fromEntity).orElse(null);
    }

}