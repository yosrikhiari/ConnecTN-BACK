package tn.esprit.spring.connectn.Services.Interfaces;

import tn.esprit.spring.connectn.DTO.LoginDTO.LoginRequestDTO;
import tn.esprit.spring.connectn.DTO.LoginDTO.LoginResponseDTO;
import tn.esprit.spring.connectn.DTO.RegistreDTO.RegisterRequestDTO;
import tn.esprit.spring.connectn.DTO.RegistreDTO.RegisterResponseDTO;
import tn.esprit.spring.connectn.DTO.UserDTO.UserUpdateDTO;
import tn.esprit.spring.connectn.Entities.User;

public interface IUserService {
    RegisterResponseDTO registerUser(RegisterRequestDTO dto);
    LoginResponseDTO loginUser(LoginRequestDTO dto);
    UserUpdateDTO updateCurrentUser(String authHeader, UserUpdateDTO dto);
}