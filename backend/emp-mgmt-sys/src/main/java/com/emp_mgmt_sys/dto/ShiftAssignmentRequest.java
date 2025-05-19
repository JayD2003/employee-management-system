package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.enums.ShiftType;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class ShiftAssignmentRequest {
    private Long userId;
    private LocalDate date;
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
