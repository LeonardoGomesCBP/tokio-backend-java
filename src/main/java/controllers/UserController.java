package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.ApiResponse;
import dto.CreateUserDTO;
import dto.CreateUserPasswordDTO;
import dto.PageDTO;
import dto.UserDTO;
import entities.User;
import jakarta.validation.Valid;
import services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        try {
            UserDTO createdUser = userService.createUser(createUserDTO);
            return new ResponseEntity<>(
                ApiResponse.success("Usuário criado com sucesso", createdUser),
                HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageDTO<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        try {
            if (!sortBy.equals("name") && !sortBy.equals("email") && !sortBy.equals("createdAt")) {
                sortBy = "name";
            }
            
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
            
            Page<UserDTO> usersPage;
            String successMessage;
            
            if (StringUtils.hasText(search)) {
                usersPage = userService.searchUsers(search, pageable);
                successMessage = "Usuários encontrados para o termo: " + search;
            } else {
                usersPage = userService.getAllUsers(pageable);
                successMessage = "Usuários recuperados com sucesso";
            }
            
            PageDTO<UserDTO> pageDTO = PageDTO.from(usersPage);
            
            return new ResponseEntity<>(
                ApiResponse.success(successMessage, pageDTO),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            // Obter informações do usuário autenticado para debugging
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                User currentUser = (User) auth.getPrincipal();
                System.out.println("DEBUG - Auth User ID: " + currentUser.getId());
                System.out.println("DEBUG - Path ID: " + id);
                System.out.println("DEBUG - Roles: " + auth.getAuthorities());
                System.out.println("DEBUG - Condição: " + (currentUser.getId().equals(id)));
            } else {
                System.out.println("DEBUG - Auth User não é uma instância de User ou é nulo");
            }
            
            UserDTO user = userService.getUserById(id);
            return new ResponseEntity<>(
                ApiResponse.success("Usuário recuperado com sucesso", user),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(
                ApiResponse.success("Usuário atualizado com sucesso", updatedUser),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserPassword(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserPasswordDTO passwordDTO) {
        try {
            UserDTO updatedUser = userService.updateUserPassword(id, passwordDTO);
            return new ResponseEntity<>(
                ApiResponse.success("Senha atualizada com sucesso", updatedUser),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(
                ApiResponse.success("Usuário excluído com sucesso", null),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            UserDTO user = userService.getUserById(currentUser.getId());
            
            return new ResponseEntity<>(
                ApiResponse.success("Usuário atual recuperado com sucesso", user),
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                ApiResponse.error(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
} 