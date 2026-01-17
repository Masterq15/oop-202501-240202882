package com.upb.agripos.service;

import com.upb.agripos.exception.AuthenticationException;
import com.upb.agripos.model.User;

/**
 * AuthServiceImpl - Implementation dari AuthService
 * 
 * Business logic untuk login & role management (FR-5)
 * 
 * Hardcoded user data untuk demo:
 * - KASIR: ismi / password123 / Ismi
 * - ADMIN: firly / admin123 / Firly
 * 
 * Created by: [Person D - Frontend/Auth]
 * Last modified: 2026-01-15
 */
public class AuthServiceImpl implements AuthService {
    
    // Hardcoded users untuk demo (dalam production, ambil dari database)
    private static final User KASIR = new User("U001", "ismi", "password123", "Ismi", User.ROLE_KASIR);
    private static final User ADMIN = new User("U002", "firly", "admin123", "Firly", User.ROLE_ADMIN);
    
    // Session management
    private User currentUser = null;
    
    @Override
    public User login(String username, String password) throws AuthenticationException {
        // Validasi input
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username tidak boleh kosong");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password tidak boleh kosong");
        }
        
        // Cek KASIR
        if (KASIR.getUsername().equals(username) && KASIR.getPassword().equals(password)) {
            this.currentUser = KASIR;
            return KASIR;
        }
        
        // Cek ADMIN
        if (ADMIN.getUsername().equals(username) && ADMIN.getPassword().equals(password)) {
            this.currentUser = ADMIN;
            return ADMIN;
        }
        
        // Login gagal
        throw new AuthenticationException("Username atau password salah!");
    }
    
    @Override
    public User getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public void logout() {
        currentUser = null;
    }
    
    @Override
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    @Override
    public boolean isKasir() {
        return currentUser != null && currentUser.isKasir();
    }
}
