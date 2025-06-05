package tn.esprit.spring.connectn.Entities.CROWDFUNDING;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Entity
@Getter @Setter
public class CampaignAnalytics {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "campaign_id")
    private CrowdFunding campaign;

    private Integer pageViews = 0;
    private Integer uniqueVisitors = 0;
    private Integer shares = 0;

    // Add these new fields
    private Integer donationsCount = 0;
    private Double averageDonationAmount = 0.0;
    private Integer returningDonors = 0;
    private Integer rewardClaims = 0;

    @ElementCollection
    @CollectionTable(name = "analytics_daily_views", joinColumns = @JoinColumn(name = "analytics_id"))
    @MapKeyColumn(name = "date")
    @Column(name = "views")
    private Map<LocalDate, Integer> dailyViews = new HashMap<>();


    @ElementCollection
    @CollectionTable(name = "daily_time_spent", joinColumns = @JoinColumn(name = "analytics_id")) // Changed table name
    @MapKeyColumn(name = "date")
    @Column(name = "total_seconds")
    private Map<LocalDate, Long> dailyTimeSpent = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "daily_view_counts", joinColumns = @JoinColumn(name = "analytics_id")) // Changed table name
    @MapKeyColumn(name = "date")
    @Column(name = "visit_count")
    private Map<LocalDate, Integer> dailyViewCounts = new HashMap<>();




    private Integer refundedDonations = 0;
    private Double totalRefundedAmount;
}