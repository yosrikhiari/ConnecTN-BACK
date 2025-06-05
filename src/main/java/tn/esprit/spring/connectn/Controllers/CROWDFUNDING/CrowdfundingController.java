package tn.esprit.spring.connectn.Controllers.CROWDFUNDING;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Converters.CampaignAnalyticsConverter;
import tn.esprit.spring.connectn.Converters.CampaignConverter;
import tn.esprit.spring.connectn.DTO.CROWDFUNDING.CrowdFundingDTO;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CampaignAnalytics;
import tn.esprit.spring.connectn.Entities.CROWDFUNDING.CrowdFunding;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CampaignAnalyticsRepository;
import tn.esprit.spring.connectn.Repository.CROWDFUNDING.CrowdfundingRepository;
import tn.esprit.spring.connectn.Services.CROWDFUNDING.CrowdfundingService;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crowdfunding")
public class CrowdfundingController {


    private final CampaignConverter campaignConverter;
    private final CrowdfundingService crowdfundingService;
    private final CrowdfundingRepository crowdfundingRepository;
    private final CampaignAnalyticsConverter analyticsConverter;

    @Autowired
    public CrowdfundingController(
                                  CampaignConverter campaignConverter,
                                  CampaignAnalyticsRepository analyticsRepository,
                                  CrowdfundingService crowdfundingService, CrowdfundingRepository crowdfundingRepository, CampaignAnalyticsConverter analyticsConverter) {

        this.campaignConverter = campaignConverter;
        this.crowdfundingService = crowdfundingService;
        this.crowdfundingRepository = crowdfundingRepository;
        this.analyticsConverter = analyticsConverter;
    }

    @PatchMapping("/{id}/cover")
    public ResponseEntity<CrowdFundingDTO> updateCampaignCover(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String imageUrl = request.get("imageUrl");
        CrowdFunding updated = crowdfundingService.updateCampaignCover(id, imageUrl);
        return ResponseEntity.ok(campaignConverter.toDto(updated));
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<CrowdFundingDTO> addCampaignMedia(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String mediaUrl = request.get("mediaUrl");
        CrowdFunding updated = crowdfundingService.addCampaignMedia(id, mediaUrl);
        return ResponseEntity.ok(campaignConverter.toDto(updated));
    }



    @PostMapping
    public ResponseEntity<CrowdFundingDTO> createCrowdfunding(
            @Valid @RequestBody CrowdFundingDTO crowdfundingDto,
            @RequestParam Long userId) {

        CrowdFunding entity = campaignConverter.toEntity(crowdfundingDto);

        // Convert and attach analytics
        if (crowdfundingDto.getAnalytics() != null) {
            CampaignAnalytics analytics = analyticsConverter.toEntity(crowdfundingDto.getAnalytics());
            entity.setAnalytics(analytics);
        }

        CrowdFunding created = crowdfundingService.createCrowdfunding(entity, userId);
        return ResponseEntity.ok(campaignConverter.toDto(created));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<String> processRefunds(@PathVariable Long id) {
        String refundStatus = crowdfundingService.processRefunds(id);
        return ResponseEntity.ok(refundStatus);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CrowdFundingDTO>> getActiveCampaigns() {
        List<CrowdFundingDTO> campaigns = crowdfundingService.getActiveCampaigns().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/ending-soon")
    public ResponseEntity<List<CrowdFundingDTO>> getCampaignsEndingSoon() {
        List<CrowdFundingDTO> campaigns = crowdfundingService.getCampaignsEndingSoon().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrowdFundingDTO> getCrowdfundingById(@PathVariable Long id) {
        return crowdfundingService.getCrowdfundingById(id)
                .map(campaignConverter::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CrowdFundingDTO> updateCrowdfunding(
            @PathVariable Long id,
            @Valid @RequestBody CrowdFundingDTO crowdfundingDto) {
        CrowdFunding entity = campaignConverter.toEntity(crowdfundingDto);
        CrowdFunding updated = crowdfundingService.updateCrowdfunding(id, entity);
        return ResponseEntity.ok(campaignConverter.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrowdfunding(@PathVariable Long id) {
        crowdfundingService.deleteCrowdfunding(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CrowdFundingDTO>> getAllCampaigns(
            @RequestParam(required = false) Boolean includeAnalytics) {

        List<CrowdFundingDTO> campaigns;
        if (includeAnalytics != null && includeAnalytics) {
            campaigns = crowdfundingService.getAllCampaignsWithAnalytics();
        } else {
            campaigns = crowdfundingService.getAllCampaignsBasic();
        }
        return ResponseEntity.ok(campaigns);
    }


    public List<CrowdFundingDTO> getAllCampaignsBasic() {
        return crowdfundingRepository.findAll().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<CrowdFundingDTO> getAllCampaignsWithAnalytics() {
        return crowdfundingRepository.findAllWithAnalytics().stream()
                .map(campaignConverter::toDto)
                .collect(Collectors.toList());
    }





    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CrowdFundingDTO> updateCampaignStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        CrowdFunding updated = crowdfundingService.updateCampaignStatus(id, status);
        return ResponseEntity.ok(campaignConverter.toDto(updated));
    }

}