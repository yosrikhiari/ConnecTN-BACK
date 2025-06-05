package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.DTO.UserDTO.UserResponseDTO;

public interface IAuthService {
    String login(String username, String password);
    boolean changePassword(String userId, String newPassword);
}
