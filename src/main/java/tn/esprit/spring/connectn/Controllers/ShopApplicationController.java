package tn.esprit.spring.connectn.Controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.ShopApplicationResponse;
import tn.esprit.spring.connectn.Entities.ApplicationStatus;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.ShopApplication;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.MarketPlace.ShopApplicationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/shop-applications")
public class ShopApplicationController {
    private final ShopApplicationService shopApplicationService;
    private final UserRepository userRepository;
    public ShopApplicationController(ShopApplicationService shopApplicationService,
                                     UserRepository userRepository) {
        this.shopApplicationService = shopApplicationService;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createApplication(
            @RequestParam Long userId,
            @RequestParam String certification,
            @RequestParam String documentType,
            @RequestParam MultipartFile[] documents) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Verify user has the correct role
            if (user.getRole() != Role.USER) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User does not have the required role"));
            }

            ShopApplication application = shopApplicationService.createApplication(
                    user, certification, documentType, documents);

            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ShopApplicationResponse>> getApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        try {
            List<ShopApplication> applications = status == null
                    ? shopApplicationService.getAllApplications()
                    : shopApplicationService.getApplicationsByStatus(status);

            return ResponseEntity.ok(applications.stream()
                    .map(ShopApplicationResponse::fromEntity)
                    .toList());
        } catch (Exception e) {
            log.error("Failed to retrieve applications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            ShopApplication application = shopApplicationService.getApplicationById(id);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to retrieve application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to retrieve application"));
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        try {
            ShopApplication application = shopApplicationService.approveApplication(id, notes);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to approve application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to approve application"));
        }
    }

    @GetMapping("/{id}/verification-status")
    public ResponseEntity<?> getVerificationStatus(@PathVariable Long id) {
        try {
            ShopApplication application = shopApplicationService.getApplicationById(id);
            return ResponseEntity.ok(Map.of(
                    "allDocumentsVerified", application.areAllDocumentsVerified(),
                    "status", application.getStatus()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get verification status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to get verification status"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            shopApplicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete application"));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long id,
            @RequestParam String notes) {
        try {
            if (notes == null || notes.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Rejection notes are required"));
            }
            ShopApplication application = shopApplicationService.rejectApplication(id, notes);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to reject application", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reject application"));
        }
    }

    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        try {
            ShopApplication application = shopApplicationService.markAsRead(id, notes);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to mark application as read", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to mark application as read"));
        }
    }

    @PutMapping("/{id}/under-review")
    public ResponseEntity<?> markUnderReview(@PathVariable Long id) {
        try {
            ShopApplication application = shopApplicationService.markUnderReview(id);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to mark application under review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to mark application under review"));
        }
    }

    @PutMapping("/{id}/needs-more-info")
    public ResponseEntity<?> markNeedsMoreInfo(
            @PathVariable Long id,
            @RequestParam String notes) {
        try {
            if (notes == null || notes.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Notes are required when requesting more information"));
            }
            ShopApplication application = shopApplicationService.markNeedsMoreInfo(id, notes);
            return ResponseEntity.ok(ShopApplicationResponse.fromEntity(application));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to mark application as needing more info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to mark application as needing more info"));
        }
    }
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ShopApplicationResponse>> getApplicationsByUser(
            @PathVariable Long userId) {
        try {
            List<ShopApplication> applications = shopApplicationService.getApplicationsByUserId(userId);

            if (applications.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(applications.stream()
                    .map(ShopApplicationResponse::fromEntity)
                    .toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            log.error("Failed to retrieve applications for user {}", userId, e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}