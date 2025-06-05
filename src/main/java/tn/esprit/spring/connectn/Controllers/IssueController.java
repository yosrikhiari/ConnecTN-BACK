package tn.esprit.spring.connectn.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.Issue;
import tn.esprit.spring.connectn.Services.Implementation.HuggingFaceService;
import tn.esprit.spring.connectn.Services.Interfaces.FileUpload;
import tn.esprit.spring.connectn.Services.Interfaces.IssueService;
import tn.esprit.spring.connectn.util.AIToCategoryMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issue")
@CrossOrigin(origins = "*")
public class IssueController {
    @Autowired
    IssueService issueService;
    @Autowired
    FileUpload fileUpload;
    @Autowired
    HuggingFaceService huggingFaceService;

    @PostMapping
    public ResponseEntity<?> creatIssue(@RequestBody Issue issue) {
        try {
            Issue createdIssue = issueService.saveIssue(issue);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdIssue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(issueService.getAllIssues());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getIssueById(@PathVariable Long id) {
        try {
            Issue issue = issueService.getIssueById(id);
            return ResponseEntity.ok(issue);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<?> searchIssuesByTitle(@PathVariable String title) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(issueService.SearchIssuesByTitle(title));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable Long id, @RequestBody Issue updatedIssue) {
        try {
            Issue issue = issueService.updateIssue(id, updatedIssue);
            return ResponseEntity.ok(issue);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/{issueId}/deleteIssue/{reporterId}")
    public ResponseEntity<?> deleteIssueByIdAndReporterId(@PathVariable Long issueId, @PathVariable Long reporterId) {
        try {
            issueService.deleteIssueByIdAndReporterId(issueId, reporterId);
            return ResponseEntity.ok("Issue deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/{issueId}/assign")
    public ResponseEntity<?> assignUserToIssue(@PathVariable Long issueId, @RequestParam Long userId) {
        try {
            Issue issue = issueService.assignUserToIssue(issueId, userId);
            return ResponseEntity.ok(issue);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<?> getIssuesByAssigneeId(@PathVariable Long assigneeId) {
        try {
            return ResponseEntity.ok(issueService.getIssuesByAssigneeId(assigneeId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching issues");
        }
    }
    @PutMapping("/{issueId}/changestatus")
    public ResponseEntity<?> updateIssueStatus(@PathVariable Long issueId, @RequestParam String status) {
        try {
            issueService.updateIssueStatus(issueId, status);
            return ResponseEntity.ok("Issue status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/{issueId}/abandon")
    public ResponseEntity<?> abandonIssue(@PathVariable Long issueId) {
        try {
            issueService.abandonIssue(issueId);
            return ResponseEntity.ok("Issue abandoned successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/uploadMedia")
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            // Upload the file and get both publicId and URL
            Map<String, String> uploadResult = fileUpload.uploadFile(file);
            String publicId = uploadResult.get("publicId");
            String url = uploadResult.get("url");

//            // Add the media (publicId and URL) to the issue
//            issueService.addMediaToIssue(issueId, publicId, url);

            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading media");
        }
    }

    @DeleteMapping("/{issueId}/deleteMedia")
    public ResponseEntity<?> deleteMedia(@PathVariable Long issueId, @RequestParam("publicId") String publicId) {
        try {
            // Delete the file from Cloudinary
            fileUpload.deleteFile(publicId);

//            // Remove the media from the issue
//            issueService.removeMediaFromIssue(issueId, publicId);

            return ResponseEntity.ok("Media deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting media");
        }
    }

    @PostMapping("/classifyImage")
    public ResponseEntity<?> classifyImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a non-empty image file");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            byte[] imageBytes = file.getBytes();
            Map<String, Object> aiResults = huggingFaceService.classifyImage(imageBytes);

            @SuppressWarnings("unchecked")
            List<String> labels = (List<String>) aiResults.get("labels");

            @SuppressWarnings("unchecked")
            Map<String, Integer> frequencies = (Map<String, Integer>) aiResults.get("frequencies");

            if (labels != null && !labels.isEmpty()) {
                String suggestedCategory = AIToCategoryMapper.mapLabelToCategory(labels, frequencies);

                // Calculate confidence based on how many labels support the chosen category
                double confidence = calculateConfidence(labels, frequencies, suggestedCategory);

                Map<String, Object> response = new HashMap<>();
                response.put("detectedLabels", labels);
                response.put("suggestedCategory", suggestedCategory);
                response.put("confidence", confidence);

                // Log the classification
                System.out.println("Image classification complete: " + response);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of(
                        "suggestedCategory", "OTHER",
                        "detectedLabels", Collections.emptyList(),
                        "confidence", 0.0,
                        "message", "No objects could be detected in the image"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing image: " + e.getMessage()));
        }
    }

    private double calculateConfidence(List<String> labels, Map<String, Integer> frequencies, String category) {
        if ("OTHER".equals(category)) {
            return 0.5; // Default confidence for OTHER
        }

        int totalLabels = labels.size();
        int categoryMatchCount = 0;
        int weightedMatches = 0;
        int totalWeight = 0;

        for (String label : labels) {
            int weight = frequencies.getOrDefault(label, 1);
            totalWeight += weight;

            if (AIToCategoryMapper.matchesCategory(label, category)) {
                categoryMatchCount++;
                weightedMatches += weight;
            }
        }

        // Base confidence on proportion of matching labels
        double labelRatio = totalLabels > 0 ? (double) categoryMatchCount / totalLabels : 0;

        // Also consider the weight of matching labels
        double weightRatio = totalWeight > 0 ? (double) weightedMatches / totalWeight : 0;

        // Combined confidence score (weighted average)
        return 0.4 * labelRatio + 0.6 * weightRatio;
    }
    @PutMapping("/{id}/verify")
    public void updateIssueVerification(@PathVariable Long id, @RequestParam boolean isVerified) {
        issueService.updateIssueVerification(id, isVerified);
    }

    @GetMapping("/non-verified")
    public List<Issue> getNonVerifiedIssues() {
        return issueService.getNonVerifiedIssues();
    }

    @GetMapping("/bounding-box")
    public ResponseEntity<List<Issue>> getIssuesInBounds(
            @RequestParam double minLat,
            @RequestParam double maxLat,
            @RequestParam double minLon,
            @RequestParam double maxLon) {
        return ResponseEntity.ok(issueService.getIssuesByBoundingBox(minLat, maxLat, minLon, maxLon));
    }

//    @PostMapping("/{id}/geocode")
//    public ResponseEntity<Void> geocodeIssue(@PathVariable Long id) {
//        issueService.geocodeIssue(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/geocode/batch")
//    public ResponseEntity<Void> batchGeocode() {
//        issueService.batchGeocodeIssues();
//        return ResponseEntity.ok().build();
//    }



}
