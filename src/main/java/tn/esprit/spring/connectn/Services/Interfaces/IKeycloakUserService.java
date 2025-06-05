package tn.esprit.spring.connectn.Services.Interfaces;

public interface IKeycloakUserService {
    public String createUser(String username,String firstname,String lastname, String email, String password);

}
