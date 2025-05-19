package com.emp_mgmt_sys.utils;

public class CustomUserPrincipal {
    private Long userId;
    private String email;

    public CustomUserPrincipal(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
