package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Entities.ShopStatus;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDetailsDto {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Long ownerId;
    private String ownerUsername;
    private String certification;
    private String category;
    private String imageUrl;
    private Float rating;
    private Integer reviews;
    private ShopStatus status;
    private LocalDateTime createdAt;
    private List<ProductDto> products;

    public static ShopDetailsDto fromEntity(Shop shop) {
        return ShopDetailsDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .description(shop.getDescription())
                .location(shop.getLocation())
                .ownerId(shop.getOwner().getId())
                .ownerUsername(shop.getOwner().getUsername())
                .certification(shop.getCertification())
                .category(shop.getCategory())
                .imageUrl(shop.getImageUrl())
                .rating(shop.getRating())
                .reviews(shop.getReviews())
                .status(shop.getStatus())
                .createdAt(shop.getCreatedAt())
                .products(shop.getProducts() != null ?
                        shop.getProducts().stream().map(ProductDto::fromEntity).toList() :
                        List.of())
                .build();
    }
}