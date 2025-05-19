package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.Status;

public class UpdateLeaveRequest {
    private Long managerId;
    private Long leaveRequestId;
    private Status status; // APPROVED or REJECTED

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
