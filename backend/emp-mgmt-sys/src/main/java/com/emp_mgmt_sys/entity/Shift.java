package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.responseDTO.ShiftResponseDTO;
import com.emp_mgmt_sys.enums.ShiftType;
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
    private User user;

    @Column(nullable = false)
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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

    public ShiftResponseDTO getDto(){
        ShiftResponseDTO dto = new ShiftResponseDTO();

        dto.setId(id);
        dto.setUserId(user.getId());
        dto.setShiftType(shiftType);
        dto.setShiftDate(shiftDate);

        return dto;
    }
}
