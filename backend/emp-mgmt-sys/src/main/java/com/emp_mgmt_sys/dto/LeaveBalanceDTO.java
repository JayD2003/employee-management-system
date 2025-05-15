package com.emp_mgmt_sys.dto;

import com.emp_mgmt_sys.entity.User;

public class LeaveBalanceDTO {
    private Long userId;

    private Integer sickLeaveBalance;

    private Integer paidLeaveBalance;

    private Integer unpaidLeaveBalance;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
