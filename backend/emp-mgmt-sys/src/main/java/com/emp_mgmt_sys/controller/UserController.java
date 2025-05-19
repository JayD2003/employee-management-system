package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.AssignEmployeeRequest;
import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.service.UserInfoService;
import com.emp_mgmt_sys.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserInfoService userService;

    // Admin can create new users
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        userService.addUser(userDTO);
        return ResponseEntity.ok("User created successfully");
    }

    // Admin can view all users
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/allManagers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllManagers() {
        return ResponseEntity.ok(userService.getAllManagers());
    }

    @GetMapping("/allEmployees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    // Admin can view a specific user
    @GetMapping("/getCurrentUser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public UserDTO getUserById() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getUserById(userId);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        String response = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(response);
    }

    // Admin can delete a user
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/assign-employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignEmployeesToManager(@RequestBody AssignEmployeeRequest assignEmployeeRequest) {
        userService.assignEmployeesToManager(assignEmployeeRequest.getManagerId(), assignEmployeeRequest.getEmployeeIds());
        return ResponseEntity.ok("Employees assigned to manager successfully");
    }

    @GetMapping("/assigned-employees/{managerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<UserDTO> getAssignedEmployees(@PathVariable Long managerId) {
        return userService.getAssignedEmployees(managerId);
    }

    @GetMapping("/assigned-employees/")
    @PreAuthorize("hasRole('MANAGER')")
    public List<UserDTO> getAssignedEmployeesForManager() {
        Long managerId = SecurityUtil.getCurrentUserId();
        return userService.getAssignedEmployees(managerId);
    }
}

