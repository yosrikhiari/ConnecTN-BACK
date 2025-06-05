package tn.esprit.spring.connectn.Services.Implementation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.connectn.DTO.LoginDTO.LoginRequestDTO;
import tn.esprit.spring.connectn.DTO.LoginDTO.LoginResponseDTO;
import tn.esprit.spring.connectn.DTO.RegistreDTO.RegisterRequestDTO;
import tn.esprit.spring.connectn.DTO.RegistreDTO.RegisterResponseDTO;
import tn.esprit.spring.connectn.DTO.UserDTO.UserResponseDTO;
import tn.esprit.spring.connectn.DTO.UserDTO.UserUpdateDTO;
import tn.esprit.spring.connectn.Entities.Role;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Exceptions.AuthenticationException;
import tn.esprit.spring.connectn.Exceptions.UserAlreadyExistsException;
import tn.esprit.spring.connectn.Exceptions.UserNotFoundException;
import tn.esprit.spring.connectn.Helper.JwtDecoder;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Interfaces.IAuthService;
import tn.esprit.spring.connectn.Services.Interfaces.IKeycloakUserService;
import tn.esprit.spring.connectn.Services.Interfaces.IUserService;

import java.util.List;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IKeycloakUserService keycloakUserService;
    private final IAuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, IKeycloakUserService keycloakUserService,IAuthService authService) {
        this.userRepository = userRepository;
        this.keycloakUserService = keycloakUserService;
        this.authService=authService;
    }

    public RegisterResponseDTO registerUser(RegisterRequestDTO dto) {
        if (!userRepository.findByUsername(dto.getUsername()).isEmpty()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (!userRepository.findByEmailAddress(dto.getEmail()).isEmpty()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        String keycloakId = keycloakUserService.createUser(
                dto.getUsername(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getEmail(),
                dto.getPassword()
        );

        User user = User.builder()
                .username(dto.getUsername())
                .emailAddress(dto.getEmail())
                .firstName(dto.getFirstname())
                .lastName(dto.getLastname())
                .keycloakId(keycloakId)
                .points(0)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);


        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmailAddress())
                .role(savedUser.getRole())
                .message("User registered successfully")
                .build();
    }
    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO dto) {
        List<User> users = userRepository.findByEmailAddress(dto.getEmailAddress());
        if (users.isEmpty()) {
            throw new AuthenticationException("User not found");
        }

        User user = users.get(0);

        String token = authService.login(dto.getEmailAddress(), dto.getPassword());

        return LoginResponseDTO.builder()
                .token(token)
                .user(UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmailAddress())
                        .role(user.getRole())
                        .build())
                .message("Login successful")
                .build();
    }
    @Transactional
    @Override
    public UserUpdateDTO updateCurrentUser(String authHeader, UserUpdateDTO dto) {
        String token = authHeader.replace("Bearer ", "");
        String keycloakId = JwtDecoder.getKeycloakId(token);

        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) throw new RuntimeException("User not found");

        dto.applyTo(user);

        user = userRepository.save(user);

        return new UserUpdateDTO(
                user.getUsername(),
                user.getEmailAddress(),
                user.getPoints(),
                user.getPhoneNumber(),
                user.getBirthdate(),
                user.getCity(),
                user.getImage(),
                user.getAboutMe(),
                user.getRole()
        );
    }

}