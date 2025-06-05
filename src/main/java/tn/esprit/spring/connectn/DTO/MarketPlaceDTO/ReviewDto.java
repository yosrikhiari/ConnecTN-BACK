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
public class ReviewDto {
    private Long id;
    private Long shopId;
    private Long userId;
    private String username;
    private String userImage;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewDto fromEntity(ShopReview review) {
        return ReviewDto.builder()
                .id(review.getId())
                .shopId(review.getShop().getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}

