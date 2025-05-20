package com.emp_mgmt_sys.exception;

public class InvalidLeaveRequestException extends RuntimeException {
    public InvalidLeaveRequestException(String message) {
        super(message);
    }
}