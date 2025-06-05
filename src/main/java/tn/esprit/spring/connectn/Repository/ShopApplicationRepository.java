package tn.esprit.spring.connectn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.UserShopDto;
import tn.esprit.spring.connectn.Entities.*;

import java.util.List;
import java.util.Optional;
@Repository
public interface ShopApplicationRepository extends JpaRepository<ShopApplication, Long> {
    // Method to find ShopApplications by their status
    List<ShopApplication> findByStatus(ApplicationStatus status);
    List<ShopApplication> findByUser_Id(Long userId);

    // OR alternatively:
    @Query("SELECT sa FROM ShopApplication sa WHERE sa.user.id = :userId")
    List<ShopApplication> findApplicationsByUserId(@Param("userId") Long userId);

}
