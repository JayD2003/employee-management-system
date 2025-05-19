package com.emp_mgmt_sys.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getUserId();
        }

        return null;
    }
    public static String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getEmail();
        }

        return null;
    }
}

