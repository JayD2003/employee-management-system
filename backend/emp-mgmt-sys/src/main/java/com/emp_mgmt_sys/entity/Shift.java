package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.ShiftDTO;
import com.emp_mgmt_sys.enums.LeaveType;
import com.emp_mgmt_sys.enums.ShiftType;
import com.emp_mgmt_sys.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // assuming User entity represents employees

    private LocalDate shiftDate; // enum for leave types

    private ShiftType shiftType;

    private LocalDateTime createdDate;

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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ShiftDTO getDto(){
        ShiftDTO dto = new ShiftDTO();

        dto.setId(id);
        dto.setUserId(user.getId());
        dto.setShiftType(shiftType);
        dto.setShiftDate(shiftDate);

        return dto;
    }
}
