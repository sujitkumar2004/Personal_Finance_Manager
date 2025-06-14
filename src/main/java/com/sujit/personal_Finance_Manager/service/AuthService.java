//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.dto.UserDTO;
//import com.sujit.personal_Finance_Manager.entity.User;
//import com.sujit.personal_Finance_Manager.exception.ConflictException;
//import com.sujit.personal_Finance_Manager.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final CategoryService categoryService;
//
//    public User register(UserDTO userDTO) {
//        if (userRepository.existsByUsername(userDTO.getUsername())) {
//            throw new ConflictException("Username (email) already exists.");
//        }
//        User user = User.builder()
//                .username(userDTO.getUsername())
//                .password(passwordEncoder.encode(userDTO.getPassword()))
//                .fullName(userDTO.getFullName())
//                .phoneNumber(userDTO.getPhoneNumber())
//                .build();
//        return userRepository.save(user);
//
//
//
//
//    }
//}

package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.dto.UserDTO;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.ConflictException;
import com.sujit.personal_Finance_Manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService; // ✅ Injected

    public User register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ConflictException("Username (email) already exists.");
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        // ✅ Create default categories after registration
        categoryService.initializeDefaultsForUser(savedUser);

        return savedUser;
    }
}
