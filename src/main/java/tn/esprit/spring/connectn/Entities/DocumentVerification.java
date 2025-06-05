package tn.esprit.spring.connectn.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "document_verifications")
public class DocumentVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_application_id", nullable = false)
    private ShopApplication shopApplication;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileContentType;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private boolean verified = false;

    private LocalDateTime uploadedAt;

    private LocalDateTime verifiedAt;

    @Column(length = 1000)
    private String verificationNotes;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }

    public void verify(String notes) {
        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
        this.verificationNotes = notes;
    }

    public void reject(String notes) {
        this.verified = false;
        this.verifiedAt = LocalDateTime.now();
        this.verificationNotes = notes;
    }
}