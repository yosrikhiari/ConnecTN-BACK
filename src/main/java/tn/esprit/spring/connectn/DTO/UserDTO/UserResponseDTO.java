package tn.esprit.spring.connectn.DTO.UserDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.User;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmailAddress();
        this.role = user.getRole();
    }
}
