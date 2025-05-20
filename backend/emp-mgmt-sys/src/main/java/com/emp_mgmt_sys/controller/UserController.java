package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.requestDTO.AssignEmployeeRequest;
import com.emp_mgmt_sys.dto.requestDTO.UserRequestDTO;
import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import com.emp_mgmt_sys.service.Impl.UserServiceImpl;
import com.emp_mgmt_sys.utils.SecurityUtil;
import jakarta.validation.Valid; // ✅ Added for request validation
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;

    // ✅ Replaced @Autowired with constructor injection (better practice)
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // ✅ Added @Valid to validate DTO fields (e.g., not null, valid email)
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        userService.addUser(userRequestDTO);
        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/allManagers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllManagers() {
        return ResponseEntity.ok(userService.getAllManagers());
    }

    @GetMapping("/allEmployees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    // ✅ Endpoint renamed from "/getCurrentUser" to "/current" (more RESTful)
    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public UserResponseDTO getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return userService.getUserById(userId);
    }

    // ✅ Improved error handling: returned error message if update fails
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        String response = userService.updateUser(id, userRequestDTO);
        if (response.equals("User not found")) {
            return ResponseEntity.badRequest().body("Update failed: User not found");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/assign-employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignEmployeesToManager(@Valid @RequestBody AssignEmployeeRequest assignEmployeeRequest) {
        userService.assignEmployeesToManager(assignEmployeeRequest.getManagerId(), assignEmployeeRequest.getEmployeeIds());
        return ResponseEntity.ok("Employees assigned to manager successfully");
    }

    @GetMapping("/assigned-employees/{managerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<UserResponseDTO> getAssignedEmployees(@PathVariable Long managerId) {
        return userService.getAssignedEmployees(managerId);
    }

    @GetMapping("/assigned-employees")
    @PreAuthorize("hasRole('MANAGER')")
    public List<UserResponseDTO> getAssignedEmployeesForManager() {
        Long managerId = SecurityUtil.getCurrentUserId();
        return userService.getAssignedEmployees(managerId);
    }
}
