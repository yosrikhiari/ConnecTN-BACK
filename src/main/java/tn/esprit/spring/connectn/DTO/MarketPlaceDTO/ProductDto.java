package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.Product;
import tn.esprit.spring.connectn.Entities.Product.ProductCategory;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer discountPrice;
    private String barcode;
    private ProductCategory category;
    private LocalDateTime createdAt;
    private Long shopId;
    private String shopName;
    private Integer salePercentage;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .discountPrice(product.getDiscountPrice())
                .barcode(product.getBarcode())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .shopId(product.getShop() != null ? product.getShop().getId() : null)
                .shopName(product.getShop() != null ? product.getShop().getName() : null)
                .salePercentage(product.getSalePercentage())
                .build();
    }
}