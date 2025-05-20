package com.emp_mgmt_sys.controller;

import com.emp_mgmt_sys.dto.requestDTO.CreateShiftSwapRequest;
import com.emp_mgmt_sys.dto.requestDTO.ShiftAssignmentRequest;
import com.emp_mgmt_sys.dto.responseDTO.ShiftSwapResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.UpdateShiftSwapRequest;
import com.emp_mgmt_sys.dto.responseDTO.ShiftResponseDTO;
import com.emp_mgmt_sys.service.Impl.ShiftServiceImpl;
import com.emp_mgmt_sys.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftServiceImpl shiftServiceImpl;

    // Assign shift for a single day - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<String> assignShift(@Valid @RequestBody ShiftAssignmentRequest request) {
        shiftServiceImpl.assignShift(request);
        return ResponseEntity.ok("Shift assigned successfully");
    }

    // Assign shifts for a whole week (Monday to Friday) - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/assign/week")
    public ResponseEntity<String> assignWeeklyShifts(@Valid @RequestBody ShiftAssignmentRequest request) {
        shiftServiceImpl.assignWeeklyShifts(request);
        return ResponseEntity.ok("Weekly shifts assigned successfully");
    }

    // Get current user's shifts based on reference date - All roles get own data
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @GetMapping("/user/shifts")
    public ResponseEntity<List<ShiftResponseDTO>> getMyShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ShiftResponseDTO> shifts = shiftServiceImpl.getEmployeeShifts(userId, referenceDate);
        return ResponseEntity.ok(shifts);
    }

    // Get current user's shift by date
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @GetMapping("/user/shift")
    public ResponseEntity<ShiftResponseDTO> getMyShiftByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = SecurityUtil.getCurrentUserId();
        ShiftResponseDTO shift = shiftServiceImpl.getEmployeeShiftByDate(userId, date);
        if (shift == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shift);
    }

    // Get current user's swap balance
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @GetMapping("/user/swap-balance")
    public ResponseEntity<Integer> getMySwapBalance() {
        Long userId = SecurityUtil.getCurrentUserId();
        int balance = shiftServiceImpl.getSwapBalance(userId);
        return ResponseEntity.ok(balance);
    }

    // Get shifts for all employees under a manager on a specific date - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager/date")
    public ResponseEntity<List<ShiftResponseDTO>> getShiftsByDateForManager(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long managerId = SecurityUtil.getCurrentUserId();
        List<ShiftResponseDTO> shifts = shiftServiceImpl.getShiftsByDateForManager(managerId, date);
        return ResponseEntity.ok(shifts);
    }

    // Create a shift swap request - Only EMPLOYEE
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/swap-request")
    public ResponseEntity<String> createSwapRequest(@Valid @RequestBody CreateShiftSwapRequest request) {
        // Always set current user ID here
        request.setUserId(SecurityUtil.getCurrentUserId());
        shiftServiceImpl.createSwapRequest(request);
        return ResponseEntity.ok("Shift swap request created");
    }

    // Update swap request status (approve/reject) - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/swap-request/status")
    public ResponseEntity<String> updateSwapRequestStatus(@Valid @RequestBody UpdateShiftSwapRequest request) {
        shiftServiceImpl.updateSwapRequestStatus(request);
        return ResponseEntity.ok("Shift swap request status updated");
    }

    // Get current user's swap requests
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @GetMapping("/swap-request/user")
    public ResponseEntity<List<ShiftSwapResponseDTO>> getMySwapRequests() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ShiftSwapResponseDTO> requests = shiftServiceImpl.getSwapRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }

    // Get swap requests for a manager by status - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/swap-request/manager/{status}")
    public ResponseEntity<List<ShiftSwapResponseDTO>> getShiftSwapRequestsForManagerOnStatus(
            @PathVariable String status) {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<ShiftSwapResponseDTO> requests = shiftServiceImpl.getShiftSwapRequestsForManagerOnStatus(managerEmail, status);
        return ResponseEntity.ok(requests);
    }

    // Get all pending swap requests for manager - Only MANAGER
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/swap-request/manager")
    public ResponseEntity<List<ShiftSwapResponseDTO>> getSwapRequestsForManager() {
        Long managerId = SecurityUtil.getCurrentUserId();
        List<ShiftSwapResponseDTO> requests = shiftServiceImpl.getSwapRequestsForManager(managerId);
        return ResponseEntity.ok(requests);
    }
}
