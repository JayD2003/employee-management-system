package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.Attendance;
import com.emp_mgmt_sys.entity.LeaveRequest;
import com.emp_mgmt_sys.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.LongUnaryOperator;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByUserIdAndStatus(Long userId, LeaveStatus leaveStatus);
}
