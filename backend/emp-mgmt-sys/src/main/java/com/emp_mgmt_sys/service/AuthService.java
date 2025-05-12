package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import com.emp_mgmt_sys.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void createAdminUser(){
        User optionalUser = userRepository.findByUserRole(UserRole.ADMIN);

        if(optionalUser == null){
            User user = new User();

            user.setName("Admin");
            user.setEmail("admin@gmail.com");
            user.setPassword("admin");
            user.setUserRole(UserRole.ADMIN);

            userRepository.save(user);
            System.out.println("Admin user created successfully");
        }else{
            System.out.println("Admin user already exists!");
        }
    }

    public UserDTO login(UserDTO user){
        Optional<User> dbUser = userRepository.findByEmail(user.getEmail());

        if(dbUser.isPresent() && user.getPassword().equals(dbUser.get().getPassword())){
            return dbUser.get().getDTO();
        }else{
            return null;
        }
    }
}
