package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "shop_applications")
@ToString(exclude = "documentVerifications")
public class ShopApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String certification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private String reviewNotes;

    @JsonManagedReference
    @OneToMany(mappedBy = "shopApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentVerification> documentVerifications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public void addDocumentVerification(DocumentVerification verification) {
        documentVerifications.add(verification);
        verification.setShopApplication(this);
    }

    public boolean areAllDocumentsVerified() {
        return !documentVerifications.isEmpty() &&
                documentVerifications.stream().allMatch(DocumentVerification::isVerified);
    }

    public void approve(String notes) {
        this.status = ApplicationStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewNotes = notes;
    }

    public void reject(String notes) {
        this.status = ApplicationStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewNotes = notes;
    }
}