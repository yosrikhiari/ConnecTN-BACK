package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.RewardTierDTO;

import tn.esprit.spring.connectn.Services.CROWDFUNDING.RewardTierService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns/{campaignId}/rewards")
public class RewardTierController {
    private final RewardTierService service;


    @Autowired
    public RewardTierController(RewardTierService service) {
        this.service = service;

    }

    @PostMapping
    public ResponseEntity<RewardTierDTO> createReward(
            @PathVariable Long campaignId,
            @RequestBody RewardTierDTO rewardDTO) {
        return ResponseEntity.ok(service.createReward(campaignId, rewardDTO));
    }

    @GetMapping
    public List<RewardTierDTO> getCampaignRewards(@PathVariable Long campaignId) {
        return service.getCampaignRewards(campaignId);
    }


}