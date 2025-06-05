package tn.esprit.spring.connectn.DTO.LoginDTO;

import lombok.Builder;
import lombok.Data;
import tn.esprit.spring.connectn.DTO.UserDTO.UserResponseDTO;

@Data
@Builder
public class LoginResponseDTO {
    private String token;
    private String message;
    private UserResponseDTO user;

}