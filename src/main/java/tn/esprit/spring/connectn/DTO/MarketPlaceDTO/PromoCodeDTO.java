package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeDTO {
    private Long id;
    private String code;
    private Long userId;
    private Long shopId;
    private String shopName;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private boolean isUsed;
    private String userEmail;
}