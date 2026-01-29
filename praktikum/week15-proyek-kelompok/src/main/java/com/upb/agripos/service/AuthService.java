package com.upb.agripos.service;

import com.upb.agripos.exception.AuthenticationException;
import com.upb.agripos.dao.UserDAO;
import com.upb.agripos.dao.UserDAOImpl;
import com.upb.agripos.model.User;

/**
 * AuthService
 * Service untuk autentikasi dan manajemen user
 * 
 * FR-5: Login dan Hak Akses
 */
public class AuthService {
    private UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
        this.currentUser = null;
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.currentUser = null;
    }

    /**
     * FR-5: Login pengguna
     */
    public User login(String username, String password) throws AuthenticationException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new AuthenticationException("Username tidak boleh kosong");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new AuthenticationException("Password tidak boleh kosong");
            }

            User user = userDAO.authenticate(username, password);
            if (user == null) {
                throw new AuthenticationException("Username atau password salah");
            }

            if (!user.isActive()) {
                throw new AuthenticationException("Akun tidak aktif");
            }

            this.currentUser = user;
            return user;

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Error saat login: " + e.getMessage(), e);
        }
    }

    /**
     * Logout pengguna
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Dapatkan user yang sedang login
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * FR-5: Cek apakah user adalah ADMIN
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * FR-5: Cek apakah user adalah KASIR
     */
    public boolean isKasir() {
        return currentUser != null && currentUser.isKasir();
    }

    /**
     * Cek apakah user sudah login
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
