package services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dto.CreateUserDTO;
import dto.CreateUserPasswordDTO;
import dto.UserDTO;

public interface UserService {
    UserDTO createUser(CreateUserDTO createUserDTO);
    List<UserDTO> getAllUsers();
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> searchUsers(String searchTerm, Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO updateUserPassword(Long id, CreateUserPasswordDTO passwordDTO);
    void deleteUser(Long id);
} 