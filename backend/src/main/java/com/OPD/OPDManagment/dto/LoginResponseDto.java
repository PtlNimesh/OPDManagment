package com.OPD.OPDManagment.dto;

public class LoginResponseDto {

    private String token;
    private String username;
    private String name;
    private String role;
    private Long entityId; // Patient ID or Doctor ID
    private String message;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String username, String name, String role, Long entityId, String message) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.role = role;
        this.entityId = entityId;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
