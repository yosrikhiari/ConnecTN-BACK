package tn.esprit.spring.connectn.Entities.CROWDFUNDING;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @Entity

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class Donation {

        private boolean anonymous = false;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long userId;

        @Column(columnDefinition = "TEXT")
        private String message;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "reward_tier_id") // This can be null
        private RewardTier rewardTier;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "crowdfunding_id", nullable = false) // Ensure not null
        private CrowdFunding crowdfunding;

        @Column(nullable = false)
        private Double amount;


        @Column(nullable = false, length = 3)
        private String currency;


        @Column(columnDefinition = "TIMESTAMP")
        private LocalDateTime timestamp;

        @Setter
        @Getter
        @Column(nullable = false)
        private String paymentIntentId;




        // Parameterized constructor
        public Donation(Long userId, Double amount, String currency, CrowdFunding crowdfunding) {
            this.userId = userId;
            this.amount = amount;
            this.currency = currency;
            this.timestamp = LocalDateTime.now();
            this.crowdfunding = crowdfunding;
        }

        public Donation() {

        }

        // In Donation.java
        public void setCampaign(CrowdFunding campaign) {
            this.crowdfunding = campaign;
        }
    }