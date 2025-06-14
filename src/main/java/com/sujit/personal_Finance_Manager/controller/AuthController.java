////package com.sujit.personal_Finance_Manager.controller;
////
////import com.sujit.personal_Finance_Manager.dto.UserDTO;
////import com.sujit.personal_Finance_Manager.entity.User;
////import com.sujit.personal_Finance_Manager.service.AuthService;
////import com.sujit.personal_Finance_Manager.service.UserService;
////import lombok.RequiredArgsConstructor;
////import org.springframework.http.HttpStatus;
////import org.springframework.security.authentication.*;
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.web.bind.annotation.*;
////
////import jakarta.servlet.http.HttpSession;
////import java.security.Principal;
////import java.util.HashMap;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api/auth")
////@RequiredArgsConstructor
////public class AuthController {
////    private final AuthService authService;
////    private final AuthenticationManager authenticationManager;
////    private final UserService userService;
////
////    @PostMapping("/register")
////    @ResponseStatus(HttpStatus.CREATED)
////    public Map<String, Object> register(@RequestBody UserDTO userDTO) {
////        User user = authService.register(userDTO);
////        Map<String, Object> response = new HashMap<>();
////        response.put("message", "User registered successfully");
////        response.put("userId", user.getId());
////        return response;
////    }
////
////    @PostMapping("/login")
////    public Map<String, Object> login(@RequestBody UserDTO userDTO, HttpSession session) {
////        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
////        Authentication auth = authenticationManager.authenticate(token);
////        SecurityContextHolder.getContext().setAuthentication(auth);
////        session.setAttribute("username", userDTO.getUsername());
////        Map<String, Object> response = new HashMap<>();
////        response.put("message", "Login successful");
////        return response;
////    }
////
////    @PostMapping("/logout")
////    public Map<String, Object> logout(HttpSession session) {
////        session.invalidate();
////        Map<String, Object> response = new HashMap<>();
////        response.put("message", "Logout successful");
////        return response;
////    }
////}

package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.config.JwtUtil;
import com.sujit.personal_Finance_Manager.dto.LoginRequest;
import com.sujit.personal_Finance_Manager.dto.LoginResponse;
import com.sujit.personal_Finance_Manager.dto.UserDTO;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.AuthService;
import com.sujit.personal_Finance_Manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
//    private final JwtBlacklist jwtBlacklist;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        User savedUser = authService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new LoginResponse(savedUser.getId(), null, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new LoginResponse(null, token, "Login successful"));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In stateless JWT, logout is handled on the client (e.g. token deletion)
        return ResponseEntity.ok().body(new LoginResponse(null, null, "Logout successful"));
    }



}
