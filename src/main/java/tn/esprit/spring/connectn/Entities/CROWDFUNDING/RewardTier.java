package tn.esprit.spring.connectn.Entities.CROWDFUNDING;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class RewardTier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CrowdFunding campaign; // Links to your existing CrowdFunding entity

    private String title; // e.g., "Early Bird Special"
    private String description; // e.g., "Get a signed thank-you note"
    private Double minAmount; // e.g., 50.0 (minimum donation to claim this reward)
    private Integer stock; // Limited quantity (e.g., 100 available)
    private Integer claimed = 0; //

}