package com.upb.agripos.service;

import com.upb.agripos.model.User;

/**
 * Interface untuk Authentication Service
 * Diimplementasikan oleh: AuthServiceImpl
 * Person D - Frontend Week 15
 */
public interface IAuthService {
    
    /**
     * Login user dengan username dan password
     * @param username username user
     * @param password password user (plaintext - untuk simulasi)
     * @return User object jika berhasil, null jika gagal
     */
    User login(String username, String password);
    
    /**
     * Logout user (clear session)
     */
    void logout();
    
    /**
     * Get current logged in user
     * @return User object atau null jika belum login
     */
    User getCurrentUser();
    
    /**
     * Check apakah user sudah login
     * @return true jika sudah login
     */
    boolean isLoggedIn();
    
    /**
     * Register user baru (untuk admin)
     * @param user user baru
     * @return true jika berhasil
     */
    boolean registerUser(User user);
    
    /**
     * Validate username format
     * @param username username
     * @return true jika valid
     */
    boolean isValidUsername(String username);
    
    /**
     * Validate password strength
     * @param password password
     * @return true jika valid
     */
    boolean isValidPassword(String password);
}
