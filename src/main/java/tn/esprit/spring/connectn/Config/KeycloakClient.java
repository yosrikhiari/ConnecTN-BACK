package tn.esprit.spring.connectn.Config;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class KeycloakClient {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String username;

    @Value("${keycloak.admin-password}")
    private String password;

    @Value("${keycloak.client-id}")
    private String clientId;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        this.keycloak = Keycloak.getInstance(serverUrl, "master", username, password, "admin-cli");
    }

    public Keycloak getKeycloak() {
        return keycloak;
    }
}


