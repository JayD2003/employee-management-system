package com.emp_mgmt_sys.dto.requestDTO;

import com.emp_mgmt_sys.enums.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateLeaveRequest {

    private Long managerId;

    @NotNull(message = "Leave Request ID is required")
    private Long leaveRequestId;

    @NotNull(message = "Status is required")
    private Status status;  // Should be APPROVED or REJECTED
 // APPROVED or REJECTED

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
