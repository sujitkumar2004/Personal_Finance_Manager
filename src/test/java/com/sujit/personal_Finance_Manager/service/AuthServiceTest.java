package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.UserDTO;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.ConflictException;
import com.sujit.personal_Finance_Manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_new_user_success() {
        UserDTO dto = new UserDTO();
        dto.setUsername("new@example.com");
        dto.setPassword("pass");
        dto.setFullName("New");
        dto.setPhoneNumber("9999999999");

        when(userRepository.existsByUsername("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = authService.register(dto);

        assertThat(saved.getUsername()).isEqualTo("new@example.com");
        assertThat(saved.getPassword()).isEqualTo("encodedpass");
    }

    @Test
    void register_duplicate_throws_conflict() {
        UserDTO dto = new UserDTO();
        dto.setUsername("dup@example.com");
        when(userRepository.existsByUsername("dup@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(dto));
    }
}

