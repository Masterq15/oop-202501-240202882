package com.upb.agripos.controller;

import com.upb.agripos.exception.AuthenticationException;
import com.upb.agripos.model.User;
import com.upb.agripos.service.AuthService;

/**
 * AuthController
 * Controller untuk login dan manajemen autentikasi
 */
public class AuthController {
    private AuthService authService;
    private AuthListener authListener;

    public interface AuthListener {
        void onLoginSuccess(User user);
        void onLoginFailed(String errorMessage);
    }

    public AuthController() {
        this.authService = new AuthService();
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void setAuthListener(AuthListener listener) {
        this.authListener = listener;
    }

    /**
     * FR-5: Login
     */
    public boolean handleLogin(String username, String password) {
        try {
            User user = authService.login(username, password);
            if (authListener != null) {
                authListener.onLoginSuccess(user);
            }
            return true;
        } catch (AuthenticationException e) {
            if (authListener != null) {
                authListener.onLoginFailed(e.getMessage());
            }
            return false;
        }
    }

    /**
     * Logout
     */
    public void handleLogout() {
        authService.logout();
    }

    /**
     * Cek apakah user sudah login
     */
    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }

    /**
     * Dapatkan user yang login
     */
    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * FR-5: Cek apakah user adalah ADMIN
     */
    public boolean isAdmin() {
        return authService.isAdmin();
    }

    /**
     * FR-5: Cek apakah user adalah KASIR
     */
    public boolean isKasir() {
        return authService.isKasir();
    }
}
