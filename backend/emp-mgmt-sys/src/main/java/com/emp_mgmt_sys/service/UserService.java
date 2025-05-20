package com.emp_mgmt_sys.service;

import com.emp_mgmt_sys.dto.requestDTO.UserRequestDTO;
import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import java.util.List;

public interface UserService {

    /**
     * Adds a new user (Admin creates user with any role).
     * @param userInfo The user information DTO.
     * @return Success message.
     */
    String addUser(UserRequestDTO userInfo);

    /**
     * Fetches all users (Admin view).
     * @return List of all users.
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Gets a specific user by ID.
     * Used for viewing own profile or admin viewing any user.
     * @param id The ID of the user.
     * @return The UserResponseDTO of the user.
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Fetches all users with role MANAGER.
     * Used when assigning employees to a manager or filtering users by role.
     * @return List of managers.
     */
    List<UserResponseDTO> getAllManagers();

    /**
     * Fetches all users with role EMPLOYEE.
     * Used by admin to assign to managers or get analytics.
     * @return List of employees.
     */
    List<UserResponseDTO> getAllEmployees();

    /**
     * Assigns multiple employees to a single manager.
     * @param managerId The ID of the manager.
     * @param employeeIds List of employee IDs to assign.
     */
    void assignEmployeesToManager(Long managerId, List<Long> employeeIds);

    /**
     * Retrieves employees assigned under a specific manager.
     * Admin and manager can view this.
     * @param managerId ID of the manager.
     * @return List of employees assigned to that manager.
     */
    List<UserResponseDTO> getAssignedEmployees(Long managerId);

    /**
     * Updates user information (Admin action).
     * @param userId ID of the user to update.
     * @param userResponseDTO Updated information.
     * @return Success message.
     */
    String updateUser(Long userId, UserRequestDTO userResponseDTO);

    /**
     * Deletes a user by ID (Admin only).
     * @param id ID of the user to delete.
     */
    void deleteUser(Long id);
}
