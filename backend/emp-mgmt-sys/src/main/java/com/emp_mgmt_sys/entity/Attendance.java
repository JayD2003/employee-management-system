package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.responseDTO.AttendanceResponseDTO;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import com.emp_mgmt_sys.enums.AttendanceStatus;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    private LocalTime clockInTime;

    private LocalTime clockOutTime;

    private Double workHours;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public AttendanceResponseDTO getDTO(){
        AttendanceResponseDTO attendanceResponseDTO = new AttendanceResponseDTO();

        attendanceResponseDTO.setUserId(user.getId());
        attendanceResponseDTO.setDate(date);
        attendanceResponseDTO.setClockInTime(clockInTime);
        attendanceResponseDTO.setClockOutTime(clockOutTime);
        attendanceResponseDTO.setWorkHours(workHours);
        attendanceResponseDTO.setStatus(status);

        return attendanceResponseDTO;
    }
}

