package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.ShiftSwapRequest;
import com.emp_mgmt_sys.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    List<ShiftSwapRequest> findByUserId(Long userId);
    List<ShiftSwapRequest> findByUserIdAndStatus(Long userId, Status status);
}
