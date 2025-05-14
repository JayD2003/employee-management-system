package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.AuthRequest;
import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.utils.UserInfoDetails;
import com.emp_mgmt_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserInfoService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserInfoService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByEmail(username); // Assuming 'email' is used as username

        // Converting UserInfo to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(UserDTO userInfo) {
        // Encode password before saving the user
        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(encoder.encode(userInfo.getPassword()));
        user.setUserRole(userInfo.getUserRole());
        repository.save(user);
        return "User Added Successfully";
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(user -> user.getDTO()).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id){
        Optional<User> userDetail = repository.findById(id);// Assuming 'email' is used as username
        return userDetail.get().getDTO();
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}