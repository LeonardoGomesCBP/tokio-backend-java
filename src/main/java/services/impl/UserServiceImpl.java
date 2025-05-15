package services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.AddressDTO;
import dto.CreateUserDTO;
import dto.CreateUserPasswordDTO;
import dto.UserDTO;
import entities.Role;
import entities.Role.ERole;
import entities.User;
import exception.EmailAlreadyExistsException;
import exception.InvalidCepException;
import exception.ResourceNotFoundException;
import repositories.RoleRepository;
import repositories.UserRepository;
import services.AddressService;
import services.CepService;
import services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private CepService cepService;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.findByEmail(createUserDTO.email()) != null) {
            throw new EmailAlreadyExistsException(createUserDTO.email());
        }
        
        if (createUserDTO.addresses() != null && !createUserDTO.addresses().isEmpty()) {
            for (AddressDTO addressDTO : createUserDTO.addresses()) {
                cepService.getCepInfo(addressDTO.zipCode());
            }
        }

        User user = new User();
        user.setName(createUserDTO.name());
        user.setEmail(createUserDTO.email());
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role não encontrada: " + ERole.ROLE_USER));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        }
        
        User savedUser = userRepository.save(user);
        
        List<AddressDTO> savedAddresses = new ArrayList<>();
        
        if (createUserDTO.addresses() != null && !createUserDTO.addresses().isEmpty()) {
            for (AddressDTO addressDTO : createUserDTO.addresses()) {
                try {
                    AddressDTO savedAddress = addressService.createAddress(savedUser.getId(), addressDTO);
                    savedAddresses.add(savedAddress);
                } catch (InvalidCepException e) {
                    throw e;
                }
            }
        }
        
        return new UserDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedAddresses,
            savedUser.getCreatedAt(),
            savedUser.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        return users.stream()
            .map(user -> {
                List<AddressDTO> addresses = addressService.getAllAddressesByUserId(user.getId());
                return new UserDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    addresses,
                    user.getCreatedAt(),
                    user.getUpdatedAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<UserDTO> userDTOs = userPage.getContent().stream()
            .map(user -> {
                List<AddressDTO> addresses = addressService.getAllAddressesByUserId(user.getId());
                return new UserDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    addresses,
                    user.getCreatedAt(),
                    user.getUpdatedAt()
                );
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        List<AddressDTO> addresses = addressService.getAllAddressesByUserId(id);
        
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            addresses,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        User existingUserWithEmail = userRepository.findByEmail(userDTO.email());
        if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(id)) {
            throw new EmailAlreadyExistsException(userDTO.email());
        }

        user.setName(userDTO.name());
        user.setEmail(userDTO.email());

        User updatedUser = userRepository.save(user);
        
        List<AddressDTO> addresses = addressService.getAllAddressesByUserId(id);

        return new UserDTO(
            updatedUser.getId(),
            updatedUser.getName(),
            updatedUser.getEmail(),
            addresses,
            updatedUser.getCreatedAt(),
            updatedUser.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserPassword(Long id, CreateUserPasswordDTO passwordDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setPassword(passwordEncoder.encode(passwordDTO.password()));
        
        User updatedUser = userRepository.save(user);
        
        List<AddressDTO> addresses = addressService.getAllAddressesByUserId(id);
        
        return new UserDTO(
            updatedUser.getId(),
            updatedUser.getName(),
            updatedUser.getEmail(),
            addresses,
            updatedUser.getCreatedAt(),
            updatedUser.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> userPage = userRepository.findByNameOrEmailContaining(searchTerm, pageable);
        
        List<UserDTO> userDTOs = userPage.getContent().stream()
            .map(user -> {
                List<AddressDTO> addresses = addressService.getAllAddressesByUserId(user.getId());
                return new UserDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    addresses,
                    user.getCreatedAt(),
                    user.getUpdatedAt()
                );
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }
} 