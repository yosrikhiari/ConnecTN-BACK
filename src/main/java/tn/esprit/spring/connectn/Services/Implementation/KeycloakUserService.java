package tn.esprit.spring.connectn.Services.Implementation;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tn.esprit.spring.connectn.Config.KeycloakClient;
import tn.esprit.spring.connectn.Services.Interfaces.IKeycloakUserService;

import java.util.List;

@Service
public class KeycloakUserService implements IKeycloakUserService {
    private final WebClient webClient;
    @Autowired
    private KeycloakClient keycloakClient;
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    public KeycloakUserService(@Value("${keycloak.server-url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }


    public String createUser(String username, String firstname, String lastname, String email, String password) {
        try {
            Keycloak keycloak = keycloakClient.getKeycloak();
            UserRepresentation user = createUserRepresentation(username, firstname, lastname, email, password);

            createUserInKeycloak(keycloak, user);
            String userId = getUserIdAfterCreation(keycloak, username);

            sendVerificationEmail(keycloak, userId);

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public boolean changePassword(String userId, String newPassword) {
        try {
            Keycloak keycloak = keycloakClient.getKeycloak();
            UserResource userResource = keycloak.realm(realm).users().get(userId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(credential);

            return true;
        } catch (Exception e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    private UserRepresentation createUserRepresentation(String username, String firstname, String lastname, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRequiredActions(List.of("VERIFY_EMAIL"));

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        return user;
    }

    private void createUserInKeycloak(Keycloak keycloak, UserRepresentation user) {
        keycloak.realm(realm).users().create(user);
    }

    private String getUserIdAfterCreation(Keycloak keycloak, String username) throws InterruptedException {
        Thread.sleep(5000);

        List<UserRepresentation> users = keycloak.realm(realm).users().search(username);
        if (users.isEmpty()) {
            throw new RuntimeException("Failed to find created user after retries");
        }
        return users.get(0).getId();
    }

    private void sendVerificationEmail(Keycloak keycloak, String userId) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        userResource.sendVerifyEmail();
    }

}