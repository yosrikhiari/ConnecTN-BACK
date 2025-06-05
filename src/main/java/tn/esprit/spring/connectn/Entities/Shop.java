package tn.esprit.spring.connectn.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, message = "Shop name must be at least 3 characters")
    @Column(nullable = false)
    private String name;

    @Size(min = 10, message = "Description must be at least 10 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String certification;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Float rating = 0.0f;

    @Column(nullable = false)
    private Integer reviews = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status;

    @Column(nullable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String imageUrl;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public void addProduct(Product product) {
        products.add(product);
        product.setShop(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setShop(null);
    }
}