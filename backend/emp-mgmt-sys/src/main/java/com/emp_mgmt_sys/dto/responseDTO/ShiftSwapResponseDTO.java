package com.emp_mgmt_sys.dto.responseDTO;

import com.emp_mgmt_sys.enums.ShiftType;
import com.emp_mgmt_sys.enums.Status;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftSwapResponseDTO {
    private Long id;

    private Long shiftId;

    private Long userId;

    private LocalDate shiftDate;

    private ShiftType shiftType;

    private Status status; // PENDING, APPROVED, REJECTED // PENDING, APPROVED, REJECTED

    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
