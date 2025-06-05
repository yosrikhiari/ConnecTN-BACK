package tn.esprit.spring.connectn.Controllers;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.PromoCodeDTO;
import tn.esprit.spring.connectn.Entities.*;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.PromoCodeRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Repository.ShopRepository;
import tn.esprit.spring.connectn.Services.MarketPlace.PromoCodeService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promo-codes")
@CrossOrigin(origins = "http://localhost:4200")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;
    private final PromoCodeRepository promoCodeRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    @Value("${promo.validity.days:7}")
    private int promoValidityDays;

    @Autowired
    public PromoCodeController(PromoCodeService promoCodeService,
                               PromoCodeRepository promoCodeRepository,
                               UserRepository userRepository,
                               ShopRepository shopRepository) {
        this.promoCodeService = promoCodeService;
        this.promoCodeRepository = promoCodeRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generatePromoCode(@RequestParam Long userId,
                                               @RequestParam Long shopId) {
        try {
            // Get user and validate points
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getPoints() < 100) {
                throw new IllegalStateException("Insufficient points. You need at least 100 points.");
            }

            // Generate the promo code through service
            PromoCode promoCode = promoCodeService.generatePromoCode(userId, shopId);

            // Deduct points
            user.setPoints(user.getPoints() - 100);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Promo code generated and 100 points deducted");
            response.put("expiresAt", promoCode.getExpiresAt());
            response.put("remainingPoints", user.getPoints());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validatePromoCode(@RequestParam String code) {
        try {
            PromoCode promoCode = promoCodeService.validatePromoCode(code);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("shopId", promoCode.getShop().getId());
            response.put("expiresAt", promoCode.getExpiresAt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(
                    Map.of("valid", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/user-active")
    public ResponseEntity<List<PromoCodeDTO>> getUserActivePromos(@RequestParam Long userId) {
        List<PromoCode> activePromos = promoCodeRepository.findByUserIdAndExpiresAtAfter(
                userId, LocalDateTime.now());

        List<PromoCodeDTO> promoDTOs = activePromos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(promoDTOs);
    }

    private PromoCodeDTO convertToDto(PromoCode promo) {
        PromoCodeDTO dto = new PromoCodeDTO();
        dto.setId(promo.getId());
        dto.setCode(promo.getCode());
        dto.setUserId(promo.getUser().getId());
        dto.setShopId(promo.getShop().getId());
        dto.setShopName(promo.getShop().getName());
        dto.setCreatedAt(promo.getCreatedAt());
        dto.setExpiryDate(promo.getExpiresAt());
        dto.setUsed(promo.isUsed());
        dto.setUserEmail(promo.getUser().getEmailAddress());
        return dto;
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        return random.ints(length, 0, chars.length())
                .mapToObj(chars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    @GetMapping("/shop-promos")
    public ResponseEntity<List<PromoCodeDTO>> getShopPromos(@RequestParam Long shopId) {
        List<PromoCode> shopPromos = promoCodeRepository.findByShopId(shopId);

        List<PromoCodeDTO> promoDTOs = shopPromos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(promoDTOs);
    }
}