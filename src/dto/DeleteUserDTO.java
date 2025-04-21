package dto;

import java.util.UUID;

public class DeleteUserDTO {
    private UUID id;
    private String username;
    private String role;
    private UUID employeeId;

    public DeleteUserDTO() {
    }

    public DeleteUserDTO(UUID id, String username, String role, UUID employeeId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.employeeId = employeeId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }
} 