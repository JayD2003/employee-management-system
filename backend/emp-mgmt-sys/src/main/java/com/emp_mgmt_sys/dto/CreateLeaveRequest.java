package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.LeaveType;

import java.time.LocalDate;

public class CreateLeaveRequest {
    private Long userId; // assuming User entity represents employees

    private LeaveType leaveType; // enum for leave types

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

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
}
