package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.LeaveRequestDTO;
import com.emp_mgmt_sys.enums.Status;
import com.emp_mgmt_sys.enums.LeaveType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // assuming User entity represents employees

    private LeaveType leaveType; // enum for leave types

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private Status status; // enum: PENDING, APPROVED, REJECTED

    private LocalDateTime requestDate;

    // getters and setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LeaveRequestDTO getDTO(){
        LeaveRequestDTO requestDTO = new LeaveRequestDTO();

        requestDTO.setId(id);
        requestDTO.setUserId(user.getId());
        requestDTO.setLeaveType(leaveType);
        requestDTO.setReason(reason);
        requestDTO.setStartDate(startDate);
        requestDTO.setEndDate(endDate);
        requestDTO.setRequestDate(requestDate);
        requestDTO.setStatus(status);

        return requestDTO;
    }
}
