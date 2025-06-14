//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void findByUsername_found() {
//        User user = User.builder().id(1L).username("abc@example.com").build();
//        when(userRepository.findByUsername("abc@example.com")).thenReturn(Optional.of(user));
//
//        Optional<User> found = userService.findByUsername("abc@example.com");
//        assertThat(found).isPresent();
//        assertThat(found.get().getUsername()).isEqualTo("abc@example.com");
//    }
//
//    @Test
//    void findByUsername_not_found() {
//        when(userRepository.findByUsername("missing@example.com")).thenReturn(Optional.empty());
//        Optional<User> found = userService.findByUsername("missing@example.com");
//        assertThat(found).isNotPresent();
//    }
//}
//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = User.builder().id(1L).username("alice").build();
//    }
//
//    @Test
//    void findByUsername_ShouldReturnUser_WhenExists() {
//        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
//
//        Optional<User> result = userService.findByUsername("alice");
//
//        assertThat(result).isPresent();
//        assertThat(result.get()).isEqualTo(user);
//    }
//
//    @Test
//    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
//        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
//
//        Optional<User> result = userService.findByUsername("bob");
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void getById_ShouldReturnUser_WhenExists() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        User result = userService.getById(1L);
//
//        assertThat(result).isEqualTo(user);
//    }
//
//    @Test
//    void getById_ShouldThrow_WhenNotExists() {
//        when(userRepository.findById(2L)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> userService.getById(2L))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("User not found");
//    }
//}
package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("alice")
                .password("encoded-password")
                .build();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("bob");

        assertThat(result).isEmpty();
    }

    @Test
    void getById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getById_ShouldThrow_WhenNotExists() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("alice");

        assertThat(userDetails.getUsername()).isEqualTo("alice");
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
