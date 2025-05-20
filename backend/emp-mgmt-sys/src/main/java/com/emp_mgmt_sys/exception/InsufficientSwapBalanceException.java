package com.emp_mgmt_sys.exception;

public class InsufficientSwapBalanceException extends RuntimeException {
    public InsufficientSwapBalanceException(String message) {
        super(message);
    }
}
