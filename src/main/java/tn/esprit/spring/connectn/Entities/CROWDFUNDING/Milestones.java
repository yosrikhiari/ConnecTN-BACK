package tn.esprit.spring.connectn.Entities.CROWDFUNDING;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Milestones
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CrowdFunding campaign;

    private String title; // e.g., "Prototype Completed"
    private Double targetAmount; // e.g., 5000.0 (triggers when reached)
    private Boolean isReached = false;
}