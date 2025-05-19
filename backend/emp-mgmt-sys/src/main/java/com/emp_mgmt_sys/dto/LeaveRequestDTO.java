package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.LeaveType;
import com.emp_mgmt_sys.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestDTO {
    private Long id;

    private Long userId; // assuming User entity represents employees

    private LeaveType leaveType; // enum for leave types

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private Status status = Status.PENDING; // enum: PENDING, APPROVED, REJECTED

    private LocalDateTime requestDate = LocalDateTime.now();

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
}
