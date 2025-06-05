package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CampaignUpdateDTO;
import tn.esprit.spring.connectn.Services.CROWDFUNDING.CampaignUpdateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/updates")
public class CampaignUpdateController {

    private final CampaignUpdateService campaignUpdateService;

    @Autowired
    public CampaignUpdateController(CampaignUpdateService campaignUpdateService) {
        this.campaignUpdateService = campaignUpdateService;
    }

    @PostMapping
    public ResponseEntity<CampaignUpdateDTO> createUpdate(
            @PathVariable Long campaignId,
            @RequestBody CampaignUpdateDTO updateDTO) {
        return ResponseEntity.ok(campaignUpdateService.createUpdate(campaignId, updateDTO));
    }


    @GetMapping
    public List<CampaignUpdateDTO> getCampaignUpdates(
            @PathVariable Long campaignId,
            @RequestParam(required = false) Long userId) {
        return campaignUpdateService.getUpdates(campaignId, userId);
    }

    @PutMapping("/{updateId}")
    public ResponseEntity<CampaignUpdateDTO> updateUpdate(
            @PathVariable Long campaignId,
            @PathVariable Long updateId,
            @RequestBody CampaignUpdateDTO updateDTO) {
        return ResponseEntity.ok(campaignUpdateService.updateUpdate(updateId, updateDTO));
    }

    @DeleteMapping("/{updateId}")
    public ResponseEntity<Void> deleteUpdate(
            @PathVariable Long campaignId,
            @PathVariable Long updateId) {
        campaignUpdateService.deleteUpdate(updateId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{updateId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long campaignId,
            @PathVariable Long updateId,
            @RequestParam Long userId) {
        campaignUpdateService.toggleLike(updateId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{updateId}/bookmark")
    public ResponseEntity<Void> toggleBookmark(
            @PathVariable Long campaignId,
            @PathVariable Long updateId,
            @RequestParam Long userId) {
        campaignUpdateService.toggleBookmark(updateId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{updateId}/engagement")
    public ResponseEntity<Map<String, Object>> getEngagementStatus(
            @PathVariable Long campaignId,
            @PathVariable Long updateId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(campaignUpdateService.getEngagementStatus(updateId, userId));
    }

    

    @PostMapping("/{updateId}/view")
    public ResponseEntity<Void> trackView(
            @PathVariable Long campaignId,
            @PathVariable Long updateId) {
        campaignUpdateService.trackView(updateId);
        return ResponseEntity.ok().build();
    }

}