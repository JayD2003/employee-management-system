package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.AuthRequest;
import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import com.emp_mgmt_sys.exception.UserNotFoundException;
import com.emp_mgmt_sys.utils.UserInfoDetails;
import com.emp_mgmt_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
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
        // Assign manager if provided
        if (userInfo.getManager() != null) {
            User manager = repository.findById(userInfo.getManager().getId())
                    .orElseThrow(() -> new UserNotFoundException("Manager not found"));
            user.setManager(manager);  // Assign manager
        }
        repository.save(user);
        return "User Added Successfully";
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(User::getDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id){
        Optional<User> userDetail = repository.findById(id);// Assuming 'email' is used as username
        if(userDetail.isPresent()) {
            return userDetail.get().getDTO();
        }else{
            throw new UserNotFoundException("User not found with id" + id);
        }
    }

    public List<UserDTO> getAllManagers() {
        List<User> managers = repository.findByUserRole(UserRole.MANAGER);
        return managers.stream().map(User::getDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getAllEmployees() {
        List<User> employees = repository.findByUserRole(UserRole.EMPLOYEE);
        return employees.stream().map(User::getDTO).collect(Collectors.toList());
    }

    public void assignEmployeesToManager(Long managerId, List<Long> employeeIds) {
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with id" + managerId));
        List<User> employeesToUpdate = new ArrayList<>();
        // Loop through each employee ID
        for (Long employeeId : employeeIds) {
            User employee = repository.findById(employeeId)
                    .orElseThrow(() -> new UserNotFoundException("Employee not found with id" + employeeId));

            // Update employee's manager
            employee.setManager(manager);
            employeesToUpdate.add(employee);
              // Save updated employee
        }
        repository.saveAll(employeesToUpdate);
    }

    // Method to get employees assigned to a specific manager
    public List<UserDTO> getAssignedEmployees(Long managerId) {
        // First, find the manager (to verify if they exist)
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with id" + managerId));

        // Now, find the employees assigned to the manager
        List<User> employees = repository.findByManager(manager);

        // Convert the list of employees to DTOs and return
        return employees.stream()
                .map(User::getDTO)
                .collect(Collectors.toList());
    }

    public String updateUser(Long userId, UserDTO userDTO) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userDTO.getPassword()));  // Update password if provided
        }

        if (userDTO.getUserRole() != null) {
            user.setUserRole(userDTO.getUserRole());  // Update role if provided
        }

        // Optionally, you can update manager if it's included in the DTO
        if (userDTO.getManager() != null) {
            User manager = repository.findById(userDTO.getManager().getId())
                    .orElseThrow(() -> new UserNotFoundException("Manager not found"));
            user.setManager(manager);
        }

        repository.save(user);
        return "User updated successfully";
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}