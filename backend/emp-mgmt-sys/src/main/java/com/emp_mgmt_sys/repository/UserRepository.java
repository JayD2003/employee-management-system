package com.emp_mgmt_sys.repository;

import com.emp_mgmt_sys.entity.User;
import com.emp_mgmt_sys.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find users by their role (e.g., MANAGER, EMPLOYEE)
    List<User> findByUserRole(UserRole userRole);

    // Find users who report to a specific manager
    List<User> findByManager(User manager);

    // Find a user by email (used as username for login)
    Optional<User> findByEmail(String email);

    // Optional Improvement:
    // Find all users by list of IDs (used for batch fetch in service layer)
    List<User> findAllById(Iterable<Long> ids);
    // This method is actually inherited from JpaRepository,
    // so no need to declare explicitly, but adding a comment to clarify.
}
