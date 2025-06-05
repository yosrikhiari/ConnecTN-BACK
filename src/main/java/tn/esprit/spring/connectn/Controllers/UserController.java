package tn.esprit.spring.connectn.Controllers;

import lombok.RequiredArgsConstructor;
import org.keycloak.jose.jwk.JWK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.UserDTO.UserResponseDTO;
import tn.esprit.spring.connectn.DTO.UserDTO.UserUpdateDTO;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Helper.JwtDecoder;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IUserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<User> getCurrentUser(@RequestParam String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PatchMapping("/update")
    public ResponseEntity<UserUpdateDTO> updateProfile(@RequestBody UserUpdateDTO dto,
                                                       @RequestHeader("Authorization") String authHeader) {
        UserUpdateDTO user=userService.updateCurrentUser(authHeader, dto);
        if (user!=null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}