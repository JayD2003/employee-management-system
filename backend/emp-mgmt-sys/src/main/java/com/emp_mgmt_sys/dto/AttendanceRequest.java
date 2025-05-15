package com.emp_mgmt_sys.dto;

import java.time.LocalTime;

public class AttendanceRequest {

    private Long userId;
    private LocalTime time;

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}

