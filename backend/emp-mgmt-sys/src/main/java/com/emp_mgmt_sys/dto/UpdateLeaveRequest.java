package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.LeaveStatus;

public class UpdateLeaveRequest {
    private Long managerId;
    private Long leaveRequestId;
    private LeaveStatus status; // APPROVED or REJECTED

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(Long leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
}
