package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.LeaveRequest;
import com.emp_mgmt_sys.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByUserIdAndStatus(Long userId, Status status);
    List<LeaveRequest> findByUserIdInAndStatus(List<Long> userIds, Status status);
    List<LeaveRequest> findByUserIdIn(List<Long> userIds);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LeaveRequest l " +
            "WHERE l.user.id = :userId AND l.status = 'APPROVED' " +
            "AND l.startDate <= :endDate AND l.endDate >= :startDate")
    boolean existsOverlappingLeaveRequest(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

}

