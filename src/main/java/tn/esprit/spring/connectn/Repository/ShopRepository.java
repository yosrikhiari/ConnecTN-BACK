package tn.esprit.spring.connectn.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.Entities.Shop;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByOwner_Id(Long ownerId);

    // For paginated version
    Page<Shop> findByOwner_Id(Long ownerId, Pageable pageable);
    // Find by name containing (case insensitive)
    Page<Shop> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find by name and location
    Page<Shop> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(
            String name, String location, Pageable pageable);

    // Find by name and category
    Page<Shop> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
            String name, String category, Pageable pageable);

    // Find by name and minimum rating
    Page<Shop> findByNameContainingIgnoreCaseAndRatingGreaterThanEqual(
            String name, Float rating, Pageable pageable);

    // Find by name, location and category
    Page<Shop> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCategoryContainingIgnoreCase(
            String name, String location, String category, Pageable pageable);

    // Find by name, location and minimum rating
    Page<Shop> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCaseAndRatingGreaterThanEqual(
            String name, String location, Float rating, Pageable pageable);

    // Find by name, category and minimum rating
    Page<Shop> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndRatingGreaterThanEqual(
            String name, String category, Float rating, Pageable pageable);

    // Find by all filters
    Page<Shop> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndRatingGreaterThanEqual(
            String name, String location, String category, Float rating, Pageable pageable);

    // Custom query to find shops with flexible filtering
    @Query("SELECT s FROM Shop s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:location IS NULL OR LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:category IS NULL OR LOWER(s.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
            "(:minRating IS NULL OR s.rating >= :minRating)")
    Page<Shop> findShopsWithFilters(
            @Param("name") String name,
            @Param("location") String location,
            @Param("category") String category,
            @Param("minRating") Float minRating,
            Pageable pageable);
}