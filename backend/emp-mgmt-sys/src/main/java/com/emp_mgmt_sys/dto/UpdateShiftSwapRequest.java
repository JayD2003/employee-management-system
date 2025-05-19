package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.Status;

public class UpdateShiftSwapRequest {
    private Long swapId;
    private Status status;

    public Long getSwapId() {
        return swapId;
    }

    public void setSwapId(Long swapId) {
        this.swapId = swapId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
