package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserRole(UserRole userRole);
    List<User> findByManager(User manager);
    Optional<User> findByEmail(String email);
}
