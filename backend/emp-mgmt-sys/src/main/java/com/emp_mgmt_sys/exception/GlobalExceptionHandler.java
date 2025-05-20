package com.emp_mgmt_sys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  // Handles exceptions globally for all controllers
public class GlobalExceptionHandler {

    // Handles 404 NOT FOUND when a resource is not found in the system
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status", "NOT_FOUND", "message", ex.getMessage()));
    }

    // Handles 403 FORBIDDEN errors when user lacks access permission (Spring Security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", "FORBIDDEN", "message", ex.getMessage()));
    }

    // Handles 400 BAD REQUEST for validation errors from request DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract all field errors and map field name -> error message
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(Map.of("status", "BAD_REQUEST", "errors", errors));
    }

    // Handles 409 CONFLICT errors such as resource already existing or conflict situations
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("status", "CONFLICT", "message", ex.getMessage()));
    }

    // Handles 400 BAD REQUEST when leave balance is insufficient for requested leave
    @ExceptionHandler(InsufficientLeaveBalanceException.class)
    public ResponseEntity<?> handleInsufficientLeaveBalance(InsufficientLeaveBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "BAD_REQUEST", "message", ex.getMessage()));
    }

    // Handles 400 BAD REQUEST for invalid leave requests (e.g., invalid date ranges, etc.)
    @ExceptionHandler(InvalidLeaveRequestException.class)
    public ResponseEntity<?> handleInvalidLeaveRequest(InvalidLeaveRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "BAD_REQUEST", "message", ex.getMessage()));
    }

    // Handles 400 BAD REQUEST when swap balance is insufficient
    @ExceptionHandler(InsufficientSwapBalanceException.class)
    public ResponseEntity<?> handleInsufficientSwapBalance(InsufficientSwapBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "BAD_REQUEST", "message", ex.getMessage()));
    }

    // Handles 400 BAD REQUEST for invalid swap requests
    @ExceptionHandler(InvalidSwapRequestException.class)
    public ResponseEntity<?> handleInvalidSwapRequest(InvalidSwapRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", "BAD_REQUEST", "message", ex.getMessage()));
    }


    // Catch-all handler for any other uncaught exceptions (500 INTERNAL SERVER ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "ERROR", "message", ex.getMessage()));
    }


}
