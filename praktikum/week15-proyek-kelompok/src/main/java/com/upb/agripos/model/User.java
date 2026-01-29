package com.upb.agripos.model;

import java.io.Serializable;

/**
 * User Model
 * Merepresentasikan pengguna sistem (Admin, Kasir)
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role; // "ADMIN" atau "KASIR"
    private boolean active;

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    public User(int id, String username, String password, String fullName, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isKasir() {
        return "KASIR".equals(role);
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
