package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.LeaveBalanceDTO;
import jakarta.persistence.*;

@Entity
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private User user;

    private Integer sickLeaveBalance = 10;

    private Integer paidLeaveBalance = 20;

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

    public LeaveBalanceDTO getDTO(){
        LeaveBalanceDTO leaveBalanceDTO = new LeaveBalanceDTO();

        leaveBalanceDTO.setUserId(user.getId());
        leaveBalanceDTO.setSickLeaveBalance(sickLeaveBalance);
        leaveBalanceDTO.setPaidLeaveBalance(paidLeaveBalance);
        leaveBalanceDTO.setUnpaidLeaveBalance(unpaidLeaveBalance);

        return leaveBalanceDTO;
    }
}

