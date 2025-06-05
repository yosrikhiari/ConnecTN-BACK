package tn.esprit.spring.connectn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.CreateReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewDto;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ReviewSummaryDto;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.MarketPlace.IShopReviewService;
import tn.esprit.spring.connectn.Services.Interfaces.IUserService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "*")
public class ShopReviewController {

    @Autowired
    private IShopReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopReviews(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ReviewDto> reviewsPage = reviewService.getShopReviews(shopId, pageable);
        ReviewSummaryDto summary = reviewService.getReviewSummary(shopId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewsPage.getContent());
        response.put("summary", summary);
        response.put("currentPage", reviewsPage.getNumber());
        response.put("totalItems", reviewsPage.getTotalElements());
        response.put("totalPages", reviewsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewDto reviewDto,
                                          @RequestParam String keycloakId) {
        try {
            User currentUser = userRepository.findByKeycloakId(keycloakId);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not authenticated"));
            }

            ReviewDto newReview = reviewService.createReview(reviewDto, currentUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        ReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewDto reviewDto,
            @RequestParam String keycloakId) {

        try {
            User currentUser = userRepository.findByKeycloakId(keycloakId);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not authenticated"));
            }

            ReviewDto updatedReview = reviewService.updateReview(id, reviewDto, currentUser.getId());
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,
            @RequestParam String keycloakId) {

        try {
            User currentUser = userRepository.findByKeycloakId(keycloakId);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "User not authenticated"));
            }

            reviewService.deleteReview(id, currentUser.getId());
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/user/current")
    public ResponseEntity<List<ReviewDto>> getCurrentUserReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Fixed: Use UserRepository instead of UserService
        List<User> users = userRepository.findByUsername(authentication.getName());
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User not found with username: " + authentication.getName());
        }
        User currentUser = users.get(0);

        List<ReviewDto> reviews = reviewService.getUserReviews(currentUser.getId());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/shop/{shopId}/summary")
    public ResponseEntity<ReviewSummaryDto> getShopReviewSummary(@PathVariable Long shopId) {
        ReviewSummaryDto summary = reviewService.getReviewSummary(shopId);
        return ResponseEntity.ok(summary);
    }


    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserShopReview(
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("shopId") Long shopId) {

        User currentUser = userRepository.findByKeycloakId(keycloakId);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get the review if it exists
        ReviewDto review = reviewService.getUserShopReview(currentUser.getId(), shopId);

        return ResponseEntity.ok(review);
    }
}