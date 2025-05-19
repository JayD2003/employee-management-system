package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.LeaveRequest;
import com.emp_mgmt_sys.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByUserIdAndStatus(Long userId, Status status);
}
