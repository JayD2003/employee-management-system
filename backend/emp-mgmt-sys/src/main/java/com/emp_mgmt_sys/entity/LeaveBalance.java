package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.responseDTO.LeaveBalanceResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

@Entity
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Min(value = 0, message = "Sick leave balance must be non-negative")
    private Integer sickLeaveBalance = 10;

    @Column(nullable = false)
    @Min(value = 0, message = "Paid leave balance must be non-negative")
    private Integer paidLeaveBalance = 20;

    @Column(nullable = false)
    @Min(value = 0, message = "Unpaid leave balance must be non-negative")
    private Integer unpaidLeaveBalance = 20;

    // getters and setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public void setSickLeaveBalance(Integer sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }

    public Integer getPaidLeaveBalance() {
        return paidLeaveBalance;
    }

    public void setPaidLeaveBalance(Integer paidLeaveBalance) {
        this.paidLeaveBalance = paidLeaveBalance;
    }

    public Integer getUnpaidLeaveBalance() {
        return unpaidLeaveBalance;
    }

    public void setUnpaidLeaveBalance(Integer unpaidLeaveBalance) {
        this.unpaidLeaveBalance = unpaidLeaveBalance;
    }

    public LeaveBalanceResponseDTO getDTO(){
        LeaveBalanceResponseDTO leaveBalanceResponseDTO = new LeaveBalanceResponseDTO();

        leaveBalanceResponseDTO.setUserId(user.getId());
        leaveBalanceResponseDTO.setSickLeaveBalance(sickLeaveBalance);
        leaveBalanceResponseDTO.setPaidLeaveBalance(paidLeaveBalance);
        leaveBalanceResponseDTO.setUnpaidLeaveBalance(unpaidLeaveBalance);

        return leaveBalanceResponseDTO;
    }
}

