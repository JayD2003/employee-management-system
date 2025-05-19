package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.*;
import com.emp_mgmt_sys.enums.Status;
import com.emp_mgmt_sys.service.ShiftService;
import com.emp_mgmt_sys.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    // Assign shift for a single day - Only ADMIN or MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<String> assignShift(@RequestBody ShiftAssignmentRequest request) {
        shiftService.assignShift(request);
        return ResponseEntity.ok("Shift assigned successfully");
    }

    // Assign shifts for a whole week (Monday to Friday) - Only ADMIN or MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/assign/week")
    public ResponseEntity<String> assignWeeklyShifts(@RequestBody ShiftAssignmentRequest request) {
        shiftService.assignWeeklyShifts(request);
        return ResponseEntity.ok("Weekly shifts assigned successfully");
    }

    // Get employee shifts for current or next week based on reference date - ADMIN, MANAGER, EMPLOYEE (only self)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShiftDTO>> getEmployeeShifts(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate) {
        List<ShiftDTO> shifts = shiftService.getEmployeeShifts(userId, referenceDate);
        return ResponseEntity.ok(shifts);
    }

    // Get employee shift on a specific date - ADMIN, MANAGER, EMPLOYEE (only self)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/user/date")
    public ResponseEntity<ShiftDTO> getEmployeeShiftByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = SecurityUtil.getCurrentUserId();
        ShiftDTO shift = shiftService.getEmployeeShiftByDate(userId, date);
        if (shift == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shift);
    }

    // Get swap balance for a user - ADMIN, MANAGER, EMPLOYEE (only self)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/user/swap-balance/{userId}")
    public ResponseEntity<Integer> getSwapBalance(@PathVariable Long userId) {
        int balance = shiftService.getSwapBalance(userId);
        return ResponseEntity.ok(balance);
    }

    // Get shifts for all employees under a manager on a specific date - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/date")
    public ResponseEntity<List<ShiftDTO>> getShiftsByDateForManager(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long managerId = SecurityUtil.getCurrentUserId();
        List<ShiftDTO> shifts = shiftService.getShiftsByDateForManager(managerId, date);
        return ResponseEntity.ok(shifts);
    }

    // Create a shift swap request - Only EMPLOYEE
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/swap-request")
    public ResponseEntity<String> createSwapRequest(@RequestBody CreateShiftSwapRequest request) {
        shiftService.createSwapRequest(request);
        return ResponseEntity.ok("Shift swap request created");
    }

    // Update swap request status (approve/reject) - Only MANAGER or ADMIN
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/swap-request/status")
    public ResponseEntity<String> updateSwapRequestStatus(@RequestBody UpdateShiftSwapRequest request) {
        shiftService.updateSwapRequestStatus(request);
        return ResponseEntity.ok("Shift swap request status updated");
    }

    // Get swap requests of a user - ADMIN, MANAGER, EMPLOYEE (only self)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @GetMapping("/swap-request/user/{userId}")
    public ResponseEntity<List<ShiftSwapRequestDTO>> getSwapRequestsByUser(@PathVariable Long userId) {
        List<ShiftSwapRequestDTO> requests = shiftService.getSwapRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }

    // Get swap requests for a manager by status (e.g. PENDING, APPROVED, REJECTED) - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/swap-request/manager/{status}")
    public ResponseEntity<List<ShiftSwapRequestDTO>> getShiftSwapRequestsForManagerOnStatus(
            @PathVariable String status) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<ShiftSwapRequestDTO> requests = shiftService.getShiftSwapRequestsForManagerOnStatus(managerEmail, status);
        return ResponseEntity.ok(requests);
    }

    // Get all pending swap requests for manager - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/swap-request/manager/")
    public ResponseEntity<List<ShiftSwapRequestDTO>> getSwapRequestsForManager() {
        Long managerId = SecurityUtil.getCurrentUserId();
        List<ShiftSwapRequestDTO> requests = shiftService.getSwapRequestsForManager(managerId);
        return ResponseEntity.ok(requests);
    }
}
