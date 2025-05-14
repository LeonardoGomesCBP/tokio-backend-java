package controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import config.TestApplication;
import dto.CreateUserDTO;
import dto.UserDTO;
import services.UserService;

@SpringBootTest(classes = TestApplication.class, 
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    private CreateUserDTO validUserDTO;
    private UserDTO createdUserDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = new CreateUserDTO(
            "Test User",
            "test@example.com",
            "password123",
            new ArrayList<>()
        );
        
        createdUserDTO = new UserDTO(
            1L,
            "Test User",
            "test@example.com",
            new ArrayList<>(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(createdUserDTO);
        when(userService.getUserById(1L)).thenReturn(createdUserDTO);
        
        List<UserDTO> userList = new ArrayList<>();
        userList.add(createdUserDTO);
        Page<UserDTO> userPage = new PageImpl<>(userList);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(userService.searchUsers(any(String.class), any(Pageable.class))).thenReturn(userPage);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.name", is("Test User")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Test User")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].name", is("Test User")))
                .andExpect(jsonPath("$.data.content[0].email", is("test@example.com")));
    }
} 