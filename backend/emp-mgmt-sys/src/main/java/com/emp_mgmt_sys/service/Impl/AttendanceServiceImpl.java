package com.emp_mgmt_sys.service.Impl;

import com.emp_mgmt_sys.dto.responseDTO.AttendanceResponseDTO;
import com.emp_mgmt_sys.dto.requestDTO.AttendanceRequest;
import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import com.emp_mgmt_sys.entity.Attendance;
import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.AttendanceStatus;
import com.emp_mgmt_sys.exception.ConflictException;
import com.emp_mgmt_sys.exception.ResourceNotFoundException;
import com.emp_mgmt_sys.repository.AttendanceRepository;
import com.emp_mgmt_sys.repository.UserRepository;
import com.emp_mgmt_sys.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void clockIn(AttendanceRequest request) {
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(request.getUserId(), LocalDate.now());
        if(existingAttendance.isPresent()){
            throw new ConflictException("Employee already clocked in today!");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setClockInTime(request.getTime());
        attendance.setStatus(AttendanceStatus.PRESENT);

        attendanceRepository.save(attendance);
    }

    @Override
    public void clockOut(AttendanceRequest request) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> optionalAttendance = attendanceRepository.findByUserIdAndDate(request.getUserId(), today);
        if (optionalAttendance.isPresent()) {
            Attendance attendance = optionalAttendance.get();

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));


            if(request.getTime().isBefore(attendance.getClockInTime())) {
                throw new ConflictException("Clock-out time cannot be before clock-in time");
            }

            attendance.setClockOutTime(request.getTime());
            Duration duration = Duration.between(attendance.getClockInTime(), request.getTime());
            double hoursWorked = duration.toMinutes() / 60.0;
            attendance.setWorkHours(hoursWorked);
            attendanceRepository.save(attendance);
        } else {
            throw new ResourceNotFoundException("Clock in is not yet completed for today");
        }
    }

    @Override
    public List<AttendanceResponseDTO> getAttendanceHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Attendance> attendances = attendanceRepository.findByUserId(userId);
        return attendances.stream().map(Attendance::getDTO).collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponseDTO> getAttendanceHistoryForManager(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        List<User> users = userRepository.findByManager(manager);

        List<Long> employeeIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        List<Attendance> allAttendances = attendanceRepository.findByUserIdIn(employeeIds);

        return allAttendances.stream()
                .map(Attendance::getDTO)
                .collect(Collectors.toList());
    }

}
