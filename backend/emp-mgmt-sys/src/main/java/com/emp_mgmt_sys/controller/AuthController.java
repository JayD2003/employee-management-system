package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.requestDTO.AuthRequest;
import com.emp_mgmt_sys.dto.requestDTO.UserRequestDTO;
import com.emp_mgmt_sys.dto.responseDTO.AuthResponse;
import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.repository.UserRepository;
import com.emp_mgmt_sys.service.Impl.UserServiceImpl;
import com.emp_mgmt_sys.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserRequestDTO userInfo) {
        return service.addUser(userInfo);
    }

    // Removed the role checks here as they are already managed in SecurityConfig

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new AuthResponse(jwtService.generateToken(authRequest.getEmail(), user.getUserRole().name(), user.getId()), user.getUserRole().name());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}

