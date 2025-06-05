package tn.esprit.spring.connectn.Controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ShopDetailsDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.UserShopDto;
import tn.esprit.spring.connectn.Entities.Product;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Entities.ShopStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.MarketPlace.ShopService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "http://localhost:4200")  // Allow cross-origin requests from Angular app
public class ShopController {
    private final ShopService shopService;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public ShopController(ShopService shopService,
                          UserRepository userRepository,
                          Cloudinary cloudinary) {
        this.shopService = shopService;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
    }


    // Get all shops
    @GetMapping
    public ResponseEntity<Page<UserShopDto>> getAllShops(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Float minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Shop> shops = shopService.getShopsWithFilters(name, location, category, minRating, pageable);
        Page<UserShopDto> shopDtos = shops.map(UserShopDto::fromEntity);
        return ResponseEntity.ok(shopDtos);
    }

    // Get all shops (without pagination) - keep for backward compatibility
    @GetMapping("/all")
    public ResponseEntity<List<Shop>> getAllShopsUnpaged() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    // Create a shop with an image
    // Create a shop with an image
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Shop> createShop(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(value = "certification", required = false) String certification,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("image") MultipartFile image) {
        try {
            // Find the owner user
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

            // Upload image
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            // Create shop
            Shop shop = new Shop();
            shop.setName(name);
            shop.setDescription(description);
            shop.setLocation(location);
            shop.setOwner(owner);
            shop.setCertification(certification);
            shop.setCategory(category);
            shop.setImageUrl(imageUrl);
            shop.setStatus(ShopStatus.APPROVED); // Default status
            shop.setRating(0.0f); // Default rating
            shop.setReviews(0); // Default reviews count

            Shop saved = shopService.createShop(shop);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Shop> updateShop(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(value = "certification", required = false) String certification,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // Find the owner user
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

            Shop shop = shopService.getShopById(id); // Get the shop to update

            if (image != null && !image.isEmpty()) {
                // Handle image upload
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                shop.setImageUrl(imageUrl);
            }

            // Update other shop fields
            shop.setName(name);
            shop.setDescription(description);
            shop.setLocation(location);
            shop.setOwner(owner);
            shop.setCertification(certification);
            shop.setCategory(category);

            Shop updatedShop = shopService.updateShop(id, shop);
            return ResponseEntity.ok(updatedShop);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    // Approve a shop
    @PutMapping("/{id}/approve")
    public ResponseEntity<Shop> approveShop(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.updateShopStatus(id, ShopStatus.APPROVED));
    }

    // Reject a shop
    @PutMapping("/{id}/reject")
    public ResponseEntity<Shop> rejectShop(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.updateShopStatus(id, ShopStatus.REJECTED));
    }

    // Delete a shop
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    // Get a shop by ID using query parameters
    @GetMapping("/shop")
    public ResponseEntity<ShopDetailsDto> getShopById(@RequestParam Long id) {
        Shop shop = shopService.getShopById(id);
        return ResponseEntity.ok(ShopDetailsDto.fromEntity(shop));
    }
    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<List<UserShopDto>> getShopsByOwner(@PathVariable Long ownerId) {
        List<Shop> shops = shopService.getShopsByOwnerId(ownerId);
        List<UserShopDto> dtos = shops.stream()
                .map(UserShopDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by-owner/{ownerId}/paginated")
    public ResponseEntity<Page<UserShopDto>> getShopsByOwnerPaginated(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Shop> shops = shopService.getShopsByOwnerId(ownerId, PageRequest.of(page, size));
        Page<UserShopDto> dtos = shops.map(UserShopDto::fromEntity);
        return ResponseEntity.ok(dtos);
    }
}