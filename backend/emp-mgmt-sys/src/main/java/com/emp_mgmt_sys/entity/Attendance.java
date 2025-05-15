package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.AttendanceDTO;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import com.emp_mgmt_sys.enums.AttendanceStatus;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate date;

    private LocalTime clockInTime;

    private LocalTime clockOutTime;

    private Double workHours;

    private AttendanceStatus status;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public Double getWorkHours() {
        return workHours;
    }

    public void setWorkHours(Double workHours) {
        this.workHours = workHours;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public AttendanceDTO getDTO(){
        AttendanceDTO attendanceDTO = new AttendanceDTO();

        attendanceDTO.setUserId(userId);
        attendanceDTO.setDate(date);
        attendanceDTO.setClockInTime(clockInTime);
        attendanceDTO.setClockOutTime(clockOutTime);
        attendanceDTO.setWorkHours(workHours);
        attendanceDTO.setStatus(status);

        return attendanceDTO;
    }
}

