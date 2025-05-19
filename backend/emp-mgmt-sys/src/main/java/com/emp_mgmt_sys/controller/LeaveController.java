package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.CreateLeaveRequest;
import com.emp_mgmt_sys.dto.LeaveBalanceDTO;
import com.emp_mgmt_sys.dto.LeaveRequestDTO;
import com.emp_mgmt_sys.dto.UpdateLeaveRequest;
import com.emp_mgmt_sys.service.LeaveService;
import com.emp_mgmt_sys.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/create-request")
    public ResponseEntity<String> createLeaveRequest(@RequestBody CreateLeaveRequest request) {
        leaveService.createLeaveRequest(request);
        return ResponseEntity.ok("Leave request submitted successfully.");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/requests/{userId}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveRequests(@PathVariable Long userId) {
        List<LeaveRequestDTO> leaveRequests = leaveService.getLeaveRequests(userId);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/balance/")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalance() {
        Long userId = SecurityUtil.getCurrentUserId();
        LeaveBalanceDTO balance = leaveService.getLeaveBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/{status}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeavesForManagerByStatus(@PathVariable String status) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestDTO> leaveRequests = leaveService.getLeaveRequestsForManagerOnLeaveStatus(managerEmail, status);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/manager/update-leave")
    public ResponseEntity<String> updateLeaveStatus(@RequestBody UpdateLeaveRequest updateRequest) {
        leaveService.updateLeaveStatus(updateRequest);
        return ResponseEntity.ok("Leave request status updated successfully.");
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/history")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveHistoryForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveRequestDTO> leaveRequests = leaveService.getLeaveRequestHistoryForManager(managerEmail);
        return ResponseEntity.ok(leaveRequests);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/balances")
    public ResponseEntity<List<LeaveBalanceDTO>> getLeaveBalancesForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<LeaveBalanceDTO> balances = leaveService.getLeaveBalancesForManager(managerEmail);
        return ResponseEntity.ok(balances);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/balance/create/{userId}")
    public ResponseEntity<String> createLeaveBalance(@PathVariable Long userId) {
        leaveService.createLeaveBalance(userId);
        return ResponseEntity.ok("Leave balance created for user: " + userId);
    }

}
