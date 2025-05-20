package com.emp_mgmt_sys.dto.requestDTO;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AttendanceRequest {

    @NotNull(message = "Time must be provided")
    private LocalTime time;

    private Long userId; // add this

    // Getters and setters
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
