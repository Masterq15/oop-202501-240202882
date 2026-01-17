package com.upb.agripos.controller;

import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthServiceImpl;
import com.upb.agripos.service.IAuthService;

/**
 * AuthController - Handle authentication logic
 * Bridge antara LoginView dan AuthService
 * Person D - Frontend Week 15
 */
public class AuthController {
    
    private IAuthService authService;
    
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Handle login button click dari LoginView
     * @param username username dari TextField
     * @param password password dari PasswordField
     * @param role role yang dipilih (KASIR atau ADMIN)
     * @return User jika login berhasil, null jika gagal
     */
    public User handleLogin(String username, String password, String role) {
        // Validasi input kosong
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username harus diisi");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: Password harus diisi");
            return null;
        }
        
        // Debug logging
        System.out.println("[DEBUG] Attempting login - Username: " + username + ", Selected Role: " + role);
        
        // Lakukan login
        User user = authService.login(username, password);
        
        // Validasi role cocok
        if (user != null) {
            System.out.println("[DEBUG] User found: " + user.getFullName() + ", Actual Role: " + user.getRole());
            
            if (!user.getRole().equalsIgnoreCase(role)) {
                System.out.println("Error: Role user tidak sesuai. User adalah " + user.getRole() + " tapi dipilih " + role);
                return null;
            }
            System.out.println("[DEBUG] Role match! Login success for: " + user.getFullName());
        } else {
            System.out.println("[DEBUG] User not found or password incorrect");
        }
        
        return user;
    }
    
    /**
     * Handle logout
     */
    public void handleLogout() {
        authService.logout();
        System.out.println("User logged out");
    }
    
    /**
     * Get current logged in user
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }
    
    /**
     * Check apakah user sudah login
     */
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }
    
    /**
     * Register user baru (hanya untuk admin)
     */
    public boolean handleRegisterUser(User user) {
        return authService.registerUser(user);
    }
    
    /**
     * Get auth service (untuk akses method lainnya)
     */
    public IAuthService getAuthService() {
        return authService;
    }
}
