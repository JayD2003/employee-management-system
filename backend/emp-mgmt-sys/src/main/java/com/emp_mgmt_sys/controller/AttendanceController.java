package com.emp_mgmt_sys.controller;


import com.emp_mgmt_sys.dto.responseDTO.AttendanceResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.AttendanceRequest;
import com.emp_mgmt_sys.service.Impl.AttendanceServiceImpl;
import com.emp_mgmt_sys.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceServiceImpl attendanceServiceImpl;

    @PostMapping("/clock-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> clockIn(@RequestBody @Valid AttendanceRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        request.setUserId(currentUserId);  // inject current user id
        attendanceServiceImpl.clockIn(request);
        return ResponseEntity.ok("Clock In saved successfully");
    }

    @PostMapping("/clock-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> clockOut(@RequestBody @Valid AttendanceRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        request.setUserId(currentUserId);  // inject current user id
        attendanceServiceImpl.clockOut(request);
        return ResponseEntity.ok("Clock Out saved successfully");
    }

    // GET /api/attendance/attendance-history/{userId}
    @GetMapping("/attendance-history/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public List<AttendanceResponseDTO> getAttendanceHistory(@PathVariable Long userId) {
        return attendanceServiceImpl.getAttendanceHistory(userId);
    }

    @GetMapping("/attendance-history/")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<AttendanceResponseDTO> getAttendanceHistoryForCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return attendanceServiceImpl.getAttendanceHistory(userId);
    }

    @GetMapping("/manager-history")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceHistoryForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<AttendanceResponseDTO> history = attendanceServiceImpl.getAttendanceHistoryForManager(managerEmail);
        return ResponseEntity.ok(history);
    }
}

