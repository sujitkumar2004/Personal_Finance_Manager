package com.sujit.personal_Finance_Manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujit.personal_Finance_Manager.config.JwtUtil;
import com.sujit.personal_Finance_Manager.config.SecurityConfig;
import com.sujit.personal_Finance_Manager.dto.*;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.AuthService;
import com.sujit.personal_Finance_Manager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("testpass");
        userDTO.setFullName("Test User");
        userDTO.setPhoneNumber("1234567890");

        loginRequest = new LoginRequest("testuser", "testpass");
    }

    @Test
    void testRegister_ShouldReturn201() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        when(authService.register(any(UserDTO.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void testLogin_Successful() throws Exception {
        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("testpass")
                .roles("USER")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken("testuser")).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void testLogin_InvalidCredentials_ShouldReturn401() throws Exception {
        doThrow(new BadCredentialsException("Invalid")).when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }
}
