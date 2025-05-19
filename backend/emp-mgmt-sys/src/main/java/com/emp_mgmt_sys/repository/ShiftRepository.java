package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.Shift;
import com.emp_mgmt_sys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findByUserIdAndShiftDate(Long userId, LocalDate shiftDate);
    List<Shift> findByUserIdAndShiftDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Shift> findByUserInAndShiftDate(List<User> users, LocalDate date);

}
