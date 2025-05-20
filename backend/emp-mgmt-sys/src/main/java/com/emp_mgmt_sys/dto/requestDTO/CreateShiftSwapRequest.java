package com.emp_mgmt_sys.dto.requestDTO;

import com.emp_mgmt_sys.enums.ShiftType;
import com.emp_mgmt_sys.enums.Status;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateShiftSwapRequest {

    @NotNull(message = "Shift ID cannot be null")
    private Long shiftId;

    private Long userId;

    @NotNull(message = "Shift date cannot be null")
    private LocalDate shiftDate;

    @NotNull(message = "Shift type cannot be null")
    private ShiftType shiftType;

    @NotNull(message = "Status cannot be null")
    private Status status; // PENDING, APPROVED, REJECTED

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}
