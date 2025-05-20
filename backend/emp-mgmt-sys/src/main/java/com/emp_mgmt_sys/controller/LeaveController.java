package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.requestDTO.CreateLeaveRequest;
import com.emp_mgmt_sys.dto.responseDTO.LeaveBalanceResponseDTO;
import com.emp_mgmt_sys.dto.responseDTO.LeaveRequestResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.UpdateLeaveRequest;
import com.emp_mgmt_sys.service.Impl.LeaveServiceImpl;
import com.emp_mgmt_sys.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveServiceImpl leaveServiceImpl;

    /**
     * Employees create leave request for themselves.
     * userId is fetched from Security context, not passed by client.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/create-request")
    public ResponseEntity<String> createLeaveRequest(@Valid @RequestBody CreateLeaveRequest request) {
        // Override userId from security context to prevent tampering
        Long userId = SecurityUtil.getCurrentUserId();
        request.setUserId(userId);

        leaveServiceImpl.createLeaveRequest(request);
        return ResponseEntity.ok("Leave request submitted successfully.");
    }

    /**
     * Get leave requests for current logged-in employee only.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveRequests() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequests(userId);
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * Get leave balance of currently logged-in employee.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalanceResponseDTO> getLeaveBalance() {
        Long userId = SecurityUtil.getCurrentUserId();
        LeaveBalanceResponseDTO balance = leaveServiceImpl.getLeaveBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /**
     * Manager fetches leave requests by status for their team.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{status}")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeavesForManagerByStatus(@PathVariable String status) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequestsForManagerOnLeaveStatus(managerEmail, status);
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * Manager updates leave request status.
     * The managerId in request DTO is overridden with authenticated manager's ID for security.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/manager/update-leave")
    public ResponseEntity<String> updateLeaveStatus(@Valid @RequestBody UpdateLeaveRequest updateRequest) {
        Long managerId = SecurityUtil.getCurrentUserId();
        updateRequest.setManagerId(managerId);

        leaveServiceImpl.updateLeaveStatus(updateRequest);
        return ResponseEntity.ok("Leave request status updated successfully.");
    }

    /**
     * Manager gets full leave request history for their team.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/history")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getLeaveHistoryForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestResponseDTO> leaveRequests = leaveServiceImpl.getLeaveRequestHistoryForManager(managerEmail);
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * Manager gets leave balances for employees under them.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/balances")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getLeaveBalancesForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveBalanceResponseDTO> balances = leaveServiceImpl.getLeaveBalancesForManager(managerEmail);
        return ResponseEntity.ok(balances);
    }

    /**
     * Admin creates leave balance record for a given user.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/balance/create/{userId}")
    public ResponseEntity<String> createLeaveBalance(@PathVariable Long userId) {
        leaveServiceImpl.createLeaveBalance(userId);
        return ResponseEntity.ok("Leave balance created for user: " + userId);
    }
}
