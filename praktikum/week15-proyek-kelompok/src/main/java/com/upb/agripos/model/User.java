package com.upb.agripos.model;

/**
 * Model class untuk User (Kasir / Admin)
 * Person D - Frontend Week 15
 */
public class User {
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String role; // "KASIR" atau "ADMIN"
    private boolean active;

    // Constructor
    public User(String userId, String username, String password, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }

    // Helper method
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isKasir() {
        return "KASIR".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return userId + " - " + fullName + " (" + role + ")";
    }
}
