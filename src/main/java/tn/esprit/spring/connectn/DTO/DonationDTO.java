package tn.esprit.spring.connectn.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DonationDTO {
    private Long id;
    private Long userId;
    private Long rewardTierId;

    @NotNull(message = "Campaign ID is required")
    private Long campaignId;

    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount is required")
    private Double amount;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;

    private String currency = "USD";

    private LocalDateTime timestamp;
    private boolean anonymous = false;
    private String paymentIntentId;
    private String message;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String cardholderName;
}