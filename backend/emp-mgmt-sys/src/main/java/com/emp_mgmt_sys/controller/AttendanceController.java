package com.emp_mgmt_sys.controller;


import com.emp_mgmt_sys.dto.AttendanceDTO;
import com.emp_mgmt_sys.dto.AttendanceRequest;
import com.emp_mgmt_sys.service.AttendanceService;
import com.emp_mgmt_sys.utils.SecurityUtil;
import com.emp_mgmt_sys.utils.UserInfoDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // POST /api/attendance/clock-in
    @PostMapping("/clock-in")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> clockIn(@RequestBody AttendanceRequest request) {
        attendanceService.clockIn(request);
        return ResponseEntity.ok("Clock In saved successfully");
    }

    // POST /api/attendance/clock-out
    @PostMapping("/clock-out")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> clockOut(@RequestBody AttendanceRequest request) {
        attendanceService.clockOut(request);
        return ResponseEntity.ok("Clock Out saved successfully");
    }

    // GET /api/attendance/attendance-history/{userId}
    @GetMapping("/attendance-history/{userId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public List<AttendanceDTO> getAttendanceHistory(@PathVariable Long userId) {
        return attendanceService.getAttendanceHistory(userId);
    }

    @GetMapping("/attendance-history/")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<AttendanceDTO> getAttendanceHistoryForCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return attendanceService.getAttendanceHistory(userId);
    }

    @GetMapping("/manager-history")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceHistoryForManager() {
        String managerEmail = SecurityUtil.getCurrentUserEmail();
        List<AttendanceDTO> history = attendanceService.getAttendanceHistoryForManager(managerEmail);
        return ResponseEntity.ok(history);
    }
}

