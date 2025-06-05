package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.MilestoneDTO;
import tn.esprit.spring.connectn.Services.CROWDFUNDING.MilestoneService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @Autowired
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping
    public ResponseEntity<MilestoneDTO> createMilestone(
            @PathVariable Long campaignId,
            @RequestBody MilestoneDTO milestoneDTO) {
        return ResponseEntity.ok(milestoneService.createMilestone(campaignId, milestoneDTO));
    }

    @GetMapping
    public List<MilestoneDTO> getCampaignMilestones(@PathVariable Long campaignId) {
        return milestoneService.getCampaignMilestones(campaignId);
    }

    @DeleteMapping("/{milestoneId}")
    public ResponseEntity<Void> deleteMilestone(
            @PathVariable Long campaignId,
            @PathVariable Long milestoneId) {
        milestoneService.deleteMilestone(milestoneId);
        return ResponseEntity.noContent().build();
    }
}