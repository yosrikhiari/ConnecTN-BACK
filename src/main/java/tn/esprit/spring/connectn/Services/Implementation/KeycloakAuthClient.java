package tn.esprit.spring.connectn.Services.Implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class KeycloakAuthClient{

    private final WebClient webClient;

    @Value("${keycloak.server-url}")
    private String baseUrl;

    public KeycloakAuthClient() {
        this.webClient = WebClient.builder().build();
    }

    public String login(String realm, String clientId, String clientSecret, String username, String password) {
        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        try {
            Map response = client.post()
                    .uri("/realms/" + realm + "/protocol/openid-connect/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", username)
                            .with("password", password)
                            .with("grant_type", "password"))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("access_token")) {
                throw new RuntimeException("Invalid login response from Keycloak");
            }

            return response.get("access_token").toString();
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}

