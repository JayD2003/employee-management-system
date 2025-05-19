package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.ShiftSwapRequestDTO;
import com.emp_mgmt_sys.enums.ShiftType;
import com.emp_mgmt_sys.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ShiftSwapRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate shiftDate;

    private ShiftType shiftType;

    private Status status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdDate;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public ShiftSwapRequestDTO getDto(){
        ShiftSwapRequestDTO dto = new ShiftSwapRequestDTO();

        dto.setId(id);
        dto.setShiftId(shift.getDto().getId());
        dto.setUserId(user.getId());
        dto.setShiftType(shiftType);
        dto.setShiftDate(shiftDate);
        dto.setStatus(status);
        dto.setCreatedDate(createdDate);

        return dto;
    }
}

