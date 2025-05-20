package com.emp_mgmt_sys.dto.requestDTO;

import com.emp_mgmt_sys.enums.ShiftType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ShiftAssignmentRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Shift type cannot be null")
    private ShiftType shiftType;

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

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}
