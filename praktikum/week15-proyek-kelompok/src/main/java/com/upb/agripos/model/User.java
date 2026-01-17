package com.upb.agripos.model;

/**
 * User Model untuk FR-5 (Login & Role)
 * 
 * Attributes:
 * - userId: unique identifier (PK)
 * - username: nama login
 * - password: password (simple, untuk demo)
 * - fullName: nama lengkap
 * - role: KASIR / ADMIN
 * 
 * Created by: [Person D - Frontend/Auth]
 * Last modified: 
 */
public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String role;  // KASIR, ADMIN

    public static final String ROLE_KASIR = "KASIR";
    public static final String ROLE_ADMIN = "ADMIN";

    public User(String userId, String username, String password, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Helper: Check role
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isKasir() {
        return ROLE_KASIR.equals(role);
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
