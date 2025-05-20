package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.responseDTO.UserResponseDTO;
import com.emp_mgmt_sys.enums.UserRole;
import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // ✅ Enforces unique and non-null email at DB level
    private String email;

    @Column(nullable = false) // ✅ Password should not be null
    private String password;

    @Column(nullable = false) // ✅ Name should not be null
    private String name;

    @Enumerated(EnumType.STRING) // ✅ Store enum value as string (e.g., MANAGER)
    @Column(nullable = false)
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Avoids unnecessary loading of manager details
    @JoinColumn(name = "manager_id")
    private User manager;

    // ------------------ Getters & Setters ------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    // ------------------ Convert Entity to DTO ------------------

    public UserResponseDTO getDTO() {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setEmail(this.getEmail());
        dto.setUserRole(this.getUserRole());

        if (this.getManager() != null) {
            UserResponseDTO.ManagerInfo managerInfo = new UserResponseDTO.ManagerInfo();
            managerInfo.setId(this.getManager().getId());
            managerInfo.setName(this.getManager().getName());
            managerInfo.setEmail(this.getManager().getEmail());
            dto.setManager(managerInfo);
        } else {
            dto.setManager(null);
        }

        return dto;
    }

}
