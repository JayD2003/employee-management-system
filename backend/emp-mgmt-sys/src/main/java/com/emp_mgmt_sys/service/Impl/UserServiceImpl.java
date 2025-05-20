package com.emp_mgmt_sys.service.Impl;

import com.emp_mgmt_sys.dto.requestDTO.UserRequestDTO;
import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import com.emp_mgmt_sys.exception.ConflictException;
import com.emp_mgmt_sys.exception.ResourceNotFoundException;
import com.emp_mgmt_sys.repository.UserRepository;
import com.emp_mgmt_sys.service.UserService;
import com.emp_mgmt_sys.utils.UserInfoDetails;
import jakarta.transaction.Transactional;  // Use jakarta.transaction.Transactional (or org.springframework.transaction.annotation.Transactional)
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
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByEmail(username); // email as username
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional  // Added transactional to ensure atomic DB operation
    public String addUser(UserRequestDTO userInfo) {

        if (repository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists."); // Prevent duplicate emails
        }

        User user = new User();
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPassword(encoder.encode(userInfo.getPassword())); // Encode password before saving
        user.setUserRole(userInfo.getUserRole());

        if (userInfo.getManagerId() != null) {
            // Added null check for manager id to avoid NullPointerException
            User manager = repository.findById(userInfo.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        }

        repository.save(user);
        return "User Added Successfully";
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return repository.findById(id)
                .map(User::getDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public List<UserResponseDTO> getAllManagers() {
        return repository.findByUserRole(UserRole.MANAGER)
                .stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getAllEmployees() {
        return repository.findByUserRole(UserRole.EMPLOYEE)
                .stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional  // Transactional to ensure atomic batch update
    public void assignEmployeesToManager(Long managerId, List<Long> employeeIds) {
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + managerId));

        // Improved: fetch all employees in a single query instead of looping and querying inside loop
        List<User> employeesToUpdate = repository.findAllById(employeeIds);

        // Check if all employee IDs are valid by comparing size
        if (employeesToUpdate.size() != employeeIds.size()) {
            // Find which IDs are missing
            List<Long> foundIds = employeesToUpdate.stream().map(User::getId).toList();
            List<Long> missingIds = employeeIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Employees not found with ids " + missingIds);
        }

        // Assign manager to all employees
        employeesToUpdate.forEach(employee -> employee.setManager(manager));
        repository.saveAll(employeesToUpdate);
    }

    @Override
    public List<UserResponseDTO> getAssignedEmployees(Long managerId) {
        User manager = repository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id " + managerId));
        List<User> employees = repository.findByManager(manager);
        return employees.stream().map(User::getDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional  // Added transactional to ensure atomic update
    public String updateUser(Long userId, UserRequestDTO userResponseDTO) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Improvement: Check for email uniqueness before updating
        if (!user.getEmail().equals(userResponseDTO.getEmail())) {
            if (repository.findByEmail(userResponseDTO.getEmail()).isPresent()) {
                throw new ConflictException("Email already exists.");
            }
            user.setEmail(userResponseDTO.getEmail());
        }

        user.setName(userResponseDTO.getName());

        if (userResponseDTO.getPassword() != null && !userResponseDTO.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userResponseDTO.getPassword())); // Encode new password if provided
        }

        if (userResponseDTO.getUserRole() != null) {
            user.setUserRole(userResponseDTO.getUserRole());
        }

        // Safe manager assignment with null checks
        if (userResponseDTO.getManagerId() != null) {
            User manager = repository.findById(userResponseDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            user.setManager(manager);
        } else {
            user.setManager(null);  // Remove manager if null passed
        }

        repository.save(user);
        return "User updated successfully";
    }

    @Override
    @Transactional  // Added transactional to ensure atomic delete
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        repository.deleteById(id);
    }
}
