package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.service.AuthService;
import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user){
        UserDTO dbUser = authService.login(user);

        if(dbUser == null){
            return new ResponseEntity<>("Wrong credentials", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Successfully logged in", HttpStatus.OK);
    }
}
