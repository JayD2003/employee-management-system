package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserRole(UserRole role);
    Optional<User> findByEmail(String email);
}
