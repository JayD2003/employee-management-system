package com.emp_mgmt_sys.service;


import com.emp_mgmt_sys.dto.AttendanceDTO;
import com.emp_mgmt_sys.dto.AttendanceRequest;
import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.entity.Attendance;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.AttendanceStatus;
import com.emp_mgmt_sys.enums.UserRole;
import com.emp_mgmt_sys.exception.UserNotFoundException;
import com.emp_mgmt_sys.repository.AttendanceRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    // Clock In
    public void clockIn(AttendanceRequest request) {

        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(request.getUserId(), LocalDate.now());
        if(existingAttendance.isPresent()){
            throw new RuntimeException("Exmployee already clocked in today!");
        }

        Attendance attendance = new Attendance();
        attendance.setUserId(request.getUserId());
        attendance.setDate(LocalDate.now());
        attendance.setClockInTime(request.getTime());
        attendance.setStatus(AttendanceStatus.PRESENT);

        attendanceRepository.save(attendance);
    }

    // Clock Out
    public void clockOut(AttendanceRequest request) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> optionalAttendance =
                attendanceRepository.findByUserIdAndDate(request.getUserId(), today);
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();
            attendance.setClockOutTime(request.getTime());
            Duration duration = Duration.between(attendance.getClockInTime(), request.getTime());
            double hoursWorked = duration.toMinutes() / 60.0;
            attendance.setWorkHours(hoursWorked);
            attendanceRepository.save(attendance);
        }else{
            throw new RuntimeException("Clock in is not yet completed for today");
        }
    }

    // Get Attendance History
    public List<AttendanceDTO> getAttendanceHistory(Long userId) {
        List<Attendance> attendances = attendanceRepository.findByUserId(userId);
        return attendances.stream().map(Attendance::getDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<UserDTO> employees = users.stream().map(User::getDTO).toList();

        List<Attendance> allAttendances = new ArrayList<>();

        for (UserDTO employee : employees) {
            List<Attendance> employeeRecords = attendanceRepository.findByUserId(employee.getId());
            allAttendances.addAll(employeeRecords);
        }

        return allAttendances.stream()
                .map(Attendance::getDTO)
                .collect(Collectors.toList());
    }


}

