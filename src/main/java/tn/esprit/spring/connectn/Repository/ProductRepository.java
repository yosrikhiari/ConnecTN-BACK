package tn.esprit.spring.connectn.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.connectn.Entities.Product;

import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("DELETE FROM Product p WHERE p.shop.id = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);
    List<Product> findByShopId(Long shopId);
    Page<Product> findByShopId(Long shopId, Pageable pageable);
    Page<Product> findByDiscountPriceGreaterThan(int discountPrice, Pageable pageable);
    Page<Product> findByCategory(Product.ProductCategory category, Pageable pageable);
}
