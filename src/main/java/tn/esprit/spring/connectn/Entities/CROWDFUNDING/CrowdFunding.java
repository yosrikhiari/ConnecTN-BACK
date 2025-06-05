package tn.esprit.spring.connectn.Entities.CROWDFUNDING;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CrowdFunding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Goal amount is required")
    @Positive(message = "Goal amount must be a positive number")
    private Double goalAmount;


    private Double currentAmount ;

    private Long challengeId;

    private Long createdBy;


    private String currency;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "campaign")
    @JsonBackReference
    private CampaignAnalytics analytics;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate endDate;

    @Transient
    private Integer durationDays;

    private String coverImageUrl;

    @ElementCollection
    private List<String> mediaUrls = new ArrayList<>();

    @PostLoad
    private void calculateDuration() {
        if (startDate != null && endDate != null) {
            this.durationDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        }
    }

    // Other fields and methods remain the same...
    // Add to problematic relationships
    @OneToMany(mappedBy = "crowdfunding")
    @JsonManagedReference
    private List<Donation> donations = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RewardTier> rewardTiers = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<Milestones> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<CampaignUpdates> updates = new ArrayList<>();

    public void addDonation(Donation donation) {
        if (!donations.contains(donation)) {
            donations.add(donation);
            donation.setCrowdfunding(this);
        }
    }

    public void addToCurrentAmount(Double amount) {
        if (this.currentAmount == null) this.currentAmount = 0.0;
        this.currentAmount += amount;
    }
}