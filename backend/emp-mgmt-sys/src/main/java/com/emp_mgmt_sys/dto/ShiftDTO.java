package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.ShiftType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftDTO {
    private Long id;

    private Long userId;

    private LocalDate shiftDate;

    private ShiftType shiftType;

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
