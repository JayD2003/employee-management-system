package com.emp_mgmt_sys.entity;

import com.emp_mgmt_sys.dto.UserDTO;
import com.emp_mgmt_sys.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private UserRole userRole;

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

    public UserDTO getDTO(){
        UserDTO dto = new UserDTO();

        dto.setId(this.id);
        dto.setName(this.name);
        dto.setEmail(this.email);
        dto.setPassword(this.password);
        dto.setUserRole(this.userRole);

        return dto;
    }

}
