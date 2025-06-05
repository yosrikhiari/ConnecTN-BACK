package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;
import lombok.*;
import tn.esprit.spring.connectn.Entities.ShopReview;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDto {
    private Float averageRating;
    private Integer totalReviews;
    private Map<Integer, Integer> ratingBreakdown; // Maps rating (1-5) to count
}