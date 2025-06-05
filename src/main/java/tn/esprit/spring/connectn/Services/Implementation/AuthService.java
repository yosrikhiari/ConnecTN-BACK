package tn.esprit.spring.connectn.Services.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Services.Interfaces.IAuthService;

@Service
public class AuthService implements IAuthService {

    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakUserService keycloakUserService;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Autowired
    public AuthService(KeycloakAuthClient keycloakAuthClient, KeycloakUserService keycloakUserService) {
        this.keycloakAuthClient = keycloakAuthClient;
        this.keycloakUserService = keycloakUserService;
    }

    @Override
    public String login(String username, String password) {
        return keycloakAuthClient.login(realm, clientId, clientSecret, username, password);
    }

    @Override
    public boolean changePassword(String userId, String newPassword) {
        return keycloakUserService.changePassword(userId, newPassword);
    }
}