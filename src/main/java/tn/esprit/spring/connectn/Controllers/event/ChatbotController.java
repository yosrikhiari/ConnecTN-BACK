package tn.esprit.spring.connectn.Controllers.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Services.event.ChatbotService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")  // Pour autoriser l'accès depuis Angular
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<?> chatWithBot(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String sender = payload.getOrDefault("sender", "user");

        try {
            String botResponse = chatbotService.sendMessageToRasa(sender, message);
            return ResponseEntity.ok(Collections.singletonMap("response", botResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
