package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.ShiftBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftBalanceRepository extends JpaRepository<ShiftBalance, Long> {
    Optional<ShiftBalance> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}
