package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {   @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @NotBlank(message = "Product name is required")
    private String name;

    @Min(value = 1, message = "Price must be at least 1")
    private Double price;

    private String description;
    private String imageUrl;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    @Min(value = 0, message = "Discount price cannot be negative")
    private int discountPrice;

    private String barcode;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private String shopName;

    @Transient
    private Integer salePercentage; // Calculated field

    public String getShopName() {
        return this.shop != null ? this.shop.getName() : null;
    }

    public Integer getSalePercentage() {
        if (this.discountPrice > 0 && this.price > 0) {
            return (int) Math.round(((this.price - this.discountPrice) / this.price) * 100);
        }
        return null;
    }
    public enum ProductCategory {
        ELECTRONICS, CLOTHING, FOOD, BOOKS, HOME, BEAUTY, SPORTS, OTHER
    }

    
}