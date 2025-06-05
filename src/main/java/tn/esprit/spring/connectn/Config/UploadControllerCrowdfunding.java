package tn.esprit.spring.connectn.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadControllerCrowdfunding {

    private final Cloudinary cloudinary;

    public UploadControllerCrowdfunding() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzrc7hsf4",
                "api_key", "631672627957979",
                "api_secret", "rQSIbikUOAypxGLngy5GqKQvUkg",
                "secure", true
        ));
    }

    @GetMapping("/campaigns/{campaignId}/media")
    public ResponseEntity<List<Map<String, Object>>> getCampaignMedia(
            @PathVariable Long campaignId) {
        try {
            String folder = "CrowdFunds/" + campaignId;

            Map<?, ?> searchResult = cloudinary.search()
                    .expression("folder:" + folder + " AND resource_type:image OR resource_type:video")
                    .execute();

            List<Map<?, ?>> resources = (List<Map<?, ?>>) searchResult.get("resources");

            List<Map<String, Object>> mediaList = new ArrayList<>();
            for (Map<?, ?> resource : resources) {
                Map<String, Object> mediaItem = new HashMap<>();
                mediaItem.put("url", resource.get("secure_url"));
                mediaItem.put("type", resource.get("resource_type"));
                mediaItem.put("public_id", resource.get("public_id"));
                mediaList.add(mediaItem);
            }

            return ResponseEntity.ok(mediaList);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/campaigns/{campaignId}/bulk")
    public ResponseEntity<Map<String, Object>> uploadCampaignFiles(
            @PathVariable Long campaignId,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "media", required = false) MultipartFile[] mediaFiles) {

        try {
            String baseFolder = "CrowdFunds/" + campaignId;
            Map<String, Object> response = new HashMap<>();

            if (cover != null && !cover.isEmpty()) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(cover.getBytes(),
                        ObjectUtils.asMap(
                                "folder", baseFolder + "/cover",
                                "public_id", "cover_" + System.currentTimeMillis(),
                                "resource_type", "auto",
                                "use_filename", true,
                                "unique_filename", false,
                                "overwrite", true
                        ));
                response.put("cover", uploadResult.get("secure_url"));
            }

            if (mediaFiles != null && mediaFiles.length > 0) {
                List<Map<String, String>> mediaUrls = new ArrayList<>();
                for (MultipartFile file : mediaFiles) {
                    if (!file.isEmpty()) {
                        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                                ObjectUtils.asMap(
                                        "folder", baseFolder + "/media",
                                        "resource_type", "auto",
                                        "use_filename", true,
                                        "unique_filename", false,
                                        "overwrite", true
                                ));
                        mediaUrls.add(Map.of(
                                "url", uploadResult.get("secure_url").toString(),
                                "type", uploadResult.get("resource_type").toString()
                        ));
                    }
                }
                response.put("media", mediaUrls);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
