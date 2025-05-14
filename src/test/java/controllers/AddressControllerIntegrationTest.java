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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import config.TestSecurityConfig;
import dto.AddressDTO;
import dto.CepDTO;
import entities.Address;
import entities.User;
import repositories.AddressRepository;
import repositories.UserRepository;
import repositories.RoleRepository;
import security.jwt.JwtUtils;
import services.AddressService;
import services.CepService;
import services.UserService;

@SpringBootTest(classes = TestApplication.class, 
    properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private AddressRepository addressRepository;
    
    @MockBean
    private RoleRepository roleRepository;
    
    @MockBean
    private CepService cepService;
    
    @MockBean
    private AddressService addressService;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtils jwtUtils;
    
    @MockBean
    private AuthController authController;
    
    private Long userId;
    private AddressDTO validAddressDTO;
    private CepDTO mockCepResponse;
    
    @BeforeEach
    void setUp() throws Exception {
        userId = 1L;
        
        validAddressDTO = new AddressDTO(
            null,
            "Test Street",
            "123",
            "Apt 456",
            "Test Neighborhood",
            "Test City",
            "TS",
            "12345-678",
            userId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        mockCepResponse = new CepDTO(
            "12345-678",
            "Test Street",
            "",
            "Test Neighborhood",
            "Test City",
            "TS"
        );
        
        when(cepService.getCepInfo("12345-678")).thenReturn(mockCepResponse);
        
        AddressDTO createdAddressDTO = new AddressDTO(
            1L,
            validAddressDTO.street(),
            validAddressDTO.number(),
            validAddressDTO.complement(),
            validAddressDTO.neighborhood(),
            validAddressDTO.city(),
            validAddressDTO.state(),
            validAddressDTO.zipCode(),
            userId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(addressService.createAddress(any(Long.class), any(AddressDTO.class))).thenReturn(createdAddressDTO);
        when(addressService.getAddressById(any(Long.class), any(Long.class))).thenReturn(createdAddressDTO);
        
        List<AddressDTO> addressList = new ArrayList<>();
        addressList.add(createdAddressDTO);
        Page<AddressDTO> addressPage = new PageImpl<>(addressList);
        when(addressService.getAllAddresses(any(Pageable.class))).thenReturn(addressPage);
        when(addressService.getAllAddressesByUserId(any(Long.class), any(Pageable.class))).thenReturn(addressPage);
        when(addressService.searchAddresses(any(String.class), any(Pageable.class))).thenReturn(addressPage);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAddress() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/addresses", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAddressDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.street", is("Test Street")))
                .andExpect(jsonPath("$.data.zipCode", is("12345-678")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAddressById() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/addresses/{addressId}", userId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.street", is("Test Street")))
                .andExpect(jsonPath("$.data.zipCode", is("12345-678")));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllAddresses() throws Exception {
        mockMvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("success")))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].street", is("Test Street")))
                .andExpect(jsonPath("$.data.content[0].zipCode", is("12345-678")));
    }
} 