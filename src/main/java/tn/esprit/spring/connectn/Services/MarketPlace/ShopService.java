package tn.esprit.spring.connectn.Services.MarketPlace;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.UserShopDto;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Entities.ShopStatus;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.ProductRepository;
import tn.esprit.spring.connectn.Repository.ShopRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;


    public ShopService(ShopRepository shopRepository, ProductRepository productRepository) {
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    public List<Shop> getAllShops() {

        return shopRepository.findAll();
    }

    public Page<Shop> getShopsWithFilters(
            String name,
            String location,
            String category,
            Float minRating,
            Pageable pageable) {

        return shopRepository.findShopsWithFilters(name, location, category, minRating, pageable);
    }

    public Shop createShop(Shop shop) {
        if (shop.getStatus() == null) {
            shop.setStatus(ShopStatus.PENDING); // New shops are pending approval by default
        }

        if (shop.getRating() == null) {
            shop.setRating(0.0f); // Default rating
        }

        if (shop.getReviews() == null) {
            shop.setReviews(0); // Default reviews count
        }

        return shopRepository.save(shop);
    }
    public List<Shop> getShopsByOwnerId(Long ownerId) {
        return shopRepository.findByOwner_Id(ownerId);
    }

    public Page<Shop> getShopsByOwnerId(Long ownerId, Pageable pageable) {
        return shopRepository.findByOwner_Id(ownerId, pageable);
    }
    public Shop updateShop(Long id, Shop updatedShop) {
        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        existingShop.setName(updatedShop.getName());
        existingShop.setDescription(updatedShop.getDescription());
        existingShop.setLocation(updatedShop.getLocation());
        existingShop.setCertification(updatedShop.getCertification());
        existingShop.setStatus(updatedShop.getStatus());
        existingShop.setOwner(updatedShop.getOwner());
        existingShop.setCategory(updatedShop.getCategory());

        // Only update image if provided
        if (updatedShop.getImageUrl() != null && !updatedShop.getImageUrl().isEmpty()) {
            existingShop.setImageUrl(updatedShop.getImageUrl());
        }

        return shopRepository.save(existingShop);
    }

    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
    }
    public Shop updateShopStatus(Long shopId, ShopStatus status) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        shop.setStatus(status);
        return shopRepository.save(shop);
    }

    public Shop updateShopRating(Long shopId, Float rating, Integer newReviews) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        shop.setRating(rating);
        shop.setReviews(newReviews);
        return shopRepository.save(shop);
    }

    @Transactional
    public void deleteShop(Long id) {
        // Delete associated products first
        productRepository.deleteAll(productRepository.findByShopId(id));
        // Then delete the shop
        shopRepository.deleteById(id);
    }


}