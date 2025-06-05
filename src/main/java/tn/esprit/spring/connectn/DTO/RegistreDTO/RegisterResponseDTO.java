package tn.esprit.spring.connectn.DTO.RegistreDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RegisterResponseDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String message;
}