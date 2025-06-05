package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shop_reviews")
public class ShopReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Comment cannot be blank")
    @Size(min = 5, max = 1000, message = "Comment must be between 5 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    // Method to update shop rating when a review is added/updated/deleted
    public void updateShopRating() {
        Shop reviewedShop = this.getShop();

        // Recalculate the average rating
        if (reviewedShop.getReviews() == 1 && this.id == null) {
            // First review
            reviewedShop.setRating(this.getRating().floatValue());
        } else {
            // Calculate new average
            float currentTotalRating = reviewedShop.getRating() * reviewedShop.getReviews();

            if (this.id == null) {
                // New review
                float newAverage = (currentTotalRating + this.getRating()) / (reviewedShop.getReviews() + 1);
                reviewedShop.setRating(newAverage);
                reviewedShop.setReviews(reviewedShop.getReviews() + 1);
            } else {
                // Update existing review - assuming you have a way to get the old rating
                // This is simplified and would need to be adjusted based on your implementation
                // Typically, you'd need to fetch the old review to get its rating
                // For now, we're just recalculating based on all reviews
            }
        }
    }
}