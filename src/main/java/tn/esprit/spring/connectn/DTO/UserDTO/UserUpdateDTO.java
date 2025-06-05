package tn.esprit.spring.connectn.DTO.UserDTO;

import lombok.*;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.User;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private String username;
    private String emailAddress;
    private Integer points;
    private Integer phoneNumber;
    private Date birthdate;
    private String city;
    private String image;
    private String aboutMe;
    private Role role;

    public void applyTo(User user) {
        if (this.username != null) {
            user.setUsername(this.username);
        }
        if (this.emailAddress != null) {
            user.setEmailAddress(this.emailAddress);
        }
        if (this.points != null) {
            user.setPoints(this.points);
        }
        if (this.phoneNumber != null) {
            user.setPhoneNumber(this.phoneNumber);
        }
        if (this.birthdate != null) {
            user.setBirthdate(this.birthdate);
        }
        if (this.city != null) {
            user.setCity(this.city);
        }
        if (this.image != null) {
            user.setImage(this.image);
        }
        if (this.aboutMe != null) {
            user.setAboutMe(this.aboutMe);
        }
        if (this.role != null) {
            user.setRole(this.role);
        }
    }
}