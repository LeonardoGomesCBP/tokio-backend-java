package services;

import java.util.List;

import dto.CreateUserDTO;
import dto.CreateUserPasswordDTO;
import dto.UserDTO;

public interface UserService {
    UserDTO createUser(CreateUserDTO createUserDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO updateUserPassword(Long id, CreateUserPasswordDTO passwordDTO);
    void deleteUser(Long id);
} 