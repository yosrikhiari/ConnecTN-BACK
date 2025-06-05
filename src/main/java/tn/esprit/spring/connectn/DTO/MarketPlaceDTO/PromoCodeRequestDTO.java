package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeRequestDTO {
    private String userId;
    private Long shopId;
    private String shopName;
    private String shopLocation;
    private String userEmail;
}
