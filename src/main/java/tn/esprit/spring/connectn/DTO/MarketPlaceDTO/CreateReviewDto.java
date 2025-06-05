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
public class CreateReviewDto {
    private Long shopId;
    private Integer rating;
    private String comment;
}