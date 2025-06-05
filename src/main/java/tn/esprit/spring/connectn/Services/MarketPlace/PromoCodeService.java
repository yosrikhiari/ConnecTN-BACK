package tn.esprit.spring.connectn.Services.MarketPlace;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.PromoCodeDTO;
import tn.esprit.spring.connectn.Entities.PromoCode;
import tn.esprit.spring.connectn.Entities.Shop;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.ResourceNotFoundException;
import tn.esprit.spring.connectn.Repository.PromoCodeRepository;
import tn.esprit.spring.connectn.Repository.ShopRepository;
import tn.esprit.spring.connectn.Repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
@Service
public class PromoCodeService {
    private final PromoCodeRepository promoCodeRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final EmailService emailService;

    @Value("${promo.validity.days:7}")
    private int promoValidityDays;

    @Autowired
    public PromoCodeService(PromoCodeRepository promoCodeRepository,
                            UserRepository userRepository,
                            ShopRepository shopRepository,
                            EmailService emailService) {
        this.promoCodeRepository = promoCodeRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.emailService = emailService;
    }

    @Transactional
    public PromoCode generatePromoCode(Long userId, Long shopId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        // Check if user has enough points
        if (user.getPoints() < 100) {
            throw new IllegalStateException("Insufficient points. You need at least 100 points to get a promo code.");
        }

        // Check if user already has an active promo for this shop
        boolean hasActivePromo = promoCodeRepository.existsByUserIdAndShopIdAndExpiresAtAfter(
                userId, shopId, LocalDateTime.now());

        if (hasActivePromo) {
            throw new IllegalStateException("You already have an active promo code for this shop.");
        }

        // Generate unique code
        String shopPrefix = shop.getName().substring(0, Math.min(3, shop.getName().length())).toUpperCase();
        String randomPart = generateRandomString(6);
        String code = shopPrefix + "-" + randomPart;

        // Create and save promo code
        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .user(user)
                .shop(shop)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(promoValidityDays))
                .isUsed(false)
                .build();

        PromoCode savedPromoCode = promoCodeRepository.save(promoCode);

        // Send email notification
        try {
            sendPromoEmail(user, shop, savedPromoCode);
        } catch (Exception e) {
            // Log the error but don't fail the transaction
            System.err.println("Failed to send promo email: " + e.getMessage());
            e.printStackTrace();
        }

        return savedPromoCode;
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendPromoEmail(User user, Shop shop, PromoCode promoCode) {
        String subject = "Your GreenPoints Promo Code for " + shop.getName();

        String emailContent =
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<div style='background-color: #4CAF50; color: white; padding: 20px; text-align: center;'>" +
                        "<h1>🎫 Your GreenPoints Reward</h1>" +
                        "</div>" +
                        "<div style='padding: 20px;'>" +
                        "<p>Hello " + user.getUsername() + ",</p>" +
                        "<p>Thank you for your ecological commitment! As a reward for your sustainability efforts, we're happy to provide you with this exclusive promo code.</p>" +
                        "<div style='background-color: #e8f5e9; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0;'>" +
                        "<h2 style='color: #2e7d32; margin-bottom: 5px;'>" + promoCode.getCode() + "</h2>" +
                        "<p style='margin-top: 5px;'><strong>Valid until:</strong> " +
                        promoCode.getExpiresAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + "</p>" +
                        "</div>" +
                        "<h3>🏪 Shop Details:</h3>" +
                        "<p><strong>Name:</strong> " + shop.getName() + "</p>" +
                        "<p><strong>Location:</strong> " + shop.getLocation() + "</p>" +
                        "<hr style='border: 1px solid #eee; margin: 20px 0;'>" +
                        "<p><em>This code can only be used physically at the store location.</em></p>" +
                        "<p>♻️ <strong>Thank you for your ecological commitment! Keep making a difference for our planet 🌍!</strong></p>" +
                        "</div>" +
                        "<div style='background-color: #f5f5f5; padding: 15px; font-size: 12px; text-align: center;'>" +
                        "<p>This email was sent to you because you redeemed your GreenPoints for a promo code.</p>" +
                        "<p>© " + LocalDateTime.now().getYear() + " ConnectN Green Initiative. All rights reserved.</p>" +
                        "</div>" +
                        "</div>";

        emailService.sendHtmlEmail(user.getEmailAddress(), subject, emailContent);
    }

    public PromoCode validatePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid promo code"));

        if (promoCode.isUsed()) {
            throw new IllegalStateException("This promo code has already been used");
        }

        if (promoCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This promo code has expired");
        }

        return promoCode;
    }

    public PromoCode markAsUsed(String code) {
        PromoCode promoCode = validatePromoCode(code);
        promoCode.setUsed(true);
        return promoCodeRepository.save(promoCode);
    }
}