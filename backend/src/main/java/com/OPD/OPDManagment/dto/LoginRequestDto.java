package com.OPD.OPDManagment.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {

    private String email;
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;

    private String role; // Optional: "PATIENT", "DOCTOR", "ADMIN"

    public LoginRequestDto() {
    }

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.username = email;
        this.password = password;
    }

    public LoginRequestDto(String email, String username, String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return (email != null && !email.isBlank()) ? email : username;
    }

    public void setEmail(String email) {
        this.email = email;
        if (this.username == null) {
            this.username = email;
        }
    }

    public String getUsername() {
        return (username != null && !username.isBlank()) ? username : email;
    }

    public void setUsername(String username) {
        this.username = username;
        if (this.email == null) {
            this.email = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
