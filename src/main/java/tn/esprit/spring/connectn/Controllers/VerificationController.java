package tn.esprit.spring.connectn.Controllers;
// VerificationController.java

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.VerificationResult;
import tn.esprit.spring.connectn.Services.MarketPlace.DocumentVerificationService;
import tn.esprit.spring.connectn.Services.MarketPlace.ShopApplicationService;

import java.util.Map;
@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final DocumentVerificationService documentVerificationService;
    private final ShopApplicationService shopApplicationService;
    @PostMapping("/document/{id}")
    public ResponseEntity<?> verifyDocument(@PathVariable Long id) {
        try {
            VerificationResult result = documentVerificationService.verifyDocument(id);
            if (result.isSuccess()) {
                return ResponseEntity.ok(Map.of(
                        "id", result.getDocument().getId(),
                        "verified", true,
                        "verificationNotes", result.getDocument().getVerificationNotes()
                ));
            }
            return ResponseEntity.badRequest().body(Map.of(
                    "error", result.getErrorMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Verification processing failed"
            ));
        }
    }
}