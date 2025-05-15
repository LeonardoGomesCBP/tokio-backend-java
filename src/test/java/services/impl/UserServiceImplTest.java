package services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import dto.AddressDTO;
import dto.CreateUserDTO;
import dto.UserDTO;
import entities.Role;
import entities.Role.ERole;
import entities.User;
import exception.ResourceNotFoundException;
import repositories.RoleRepository;
import repositories.UserRepository;
import services.AddressService;
import services.CepService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressService addressService;
    
    @Mock
    private CepService cepService;
    
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private CreateUserDTO createUserDTO;
    private List<AddressDTO> addresses;
    private LocalDateTime now;
    private Role userRole;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        userRole = new Role(ERole.ROLE_USER);
        userRole.setId(1L);
        
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        addresses = new ArrayList<>();
        
        userDTO = new UserDTO(
            1L,
            "Test User",
            "test@example.com",
            addresses,
            now,
            now
        );
        
        createUserDTO = new CreateUserDTO(
            "Test User",
            "test@example.com",
            "password",
            addresses
        );
    }

    @Test
    void testCreateUser() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        // Act
        UserDTO result = userService.createUser(createUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO.id(), result.id());
        assertEquals(userDTO.name(), result.name());
        assertEquals(userDTO.email(), result.email());
        
        verify(userRepository).findByEmail(createUserDTO.email());
        verify(passwordEncoder).encode(createUserDTO.password());
        verify(roleRepository).findByName(ERole.ROLE_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(addressService.getAllAddressesByUserId(anyLong())).thenReturn(addresses);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDTO.id(), result.get(0).id());
        assertEquals(userDTO.name(), result.get(0).name());
        assertEquals(userDTO.email(), result.get(0).email());
        
        verify(userRepository).findAll();
        verify(addressService).getAllAddressesByUserId(user.getId());
    }

    @Test
    void testGetAllUsersPaginated() {
        // Arrange
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(addressService.getAllAddressesByUserId(anyLong())).thenReturn(addresses);

        // Act
        Page<UserDTO> result = userService.getAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userDTO.id(), result.getContent().get(0).id());
        assertEquals(userDTO.name(), result.getContent().get(0).name());
        assertEquals(userDTO.email(), result.getContent().get(0).email());
        
        verify(userRepository).findAll(pageable);
        verify(addressService).getAllAddressesByUserId(user.getId());
    }

    @Test
    void testSearchUsers() {
        // Arrange
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "test";
        
        when(userRepository.findByNameOrEmailContaining(searchTerm, pageable)).thenReturn(userPage);
        when(addressService.getAllAddressesByUserId(anyLong())).thenReturn(addresses);

        // Act
        Page<UserDTO> result = userService.searchUsers(searchTerm, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userDTO.id(), result.getContent().get(0).id());
        assertEquals(userDTO.name(), result.getContent().get(0).name());
        assertEquals(userDTO.email(), result.getContent().get(0).email());
        
        verify(userRepository).findByNameOrEmailContaining(searchTerm, pageable);
        verify(addressService).getAllAddressesByUserId(user.getId());
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressService.getAllAddressesByUserId(1L)).thenReturn(addresses);

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO.id(), result.id());
        assertEquals(userDTO.name(), result.name());
        assertEquals(userDTO.email(), result.email());
        
        verify(userRepository).findById(1L);
        verify(addressService).getAllAddressesByUserId(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(99L);
        });
        
        verify(userRepository).findById(99L);
        verifyNoInteractions(addressService);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(addressService.getAllAddressesByUserId(1L)).thenReturn(addresses);

        // Act
        UserDTO result = userService.updateUser(1L, userDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO.id(), result.id());
        assertEquals(userDTO.name(), result.name());
        assertEquals(userDTO.email(), result.email());
        
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(addressService).getAllAddressesByUserId(1L);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(99L);
        });
        
        verify(userRepository).findById(99L);
        verify(userRepository, never()).delete(any(User.class));
    }
} 