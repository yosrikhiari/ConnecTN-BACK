package tn.esprit.spring.connectn.Services.event;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String rasaWebhookUrl = "http://localhost:5055/webhook";  // Correspond à l'URL dans la config de Rasa

    public String sendMessageToRasa(String sender, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("sender", sender);
        payload.put("message", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(rasaWebhookUrl, request, String.class);
        return response.getBody();
    }
}
