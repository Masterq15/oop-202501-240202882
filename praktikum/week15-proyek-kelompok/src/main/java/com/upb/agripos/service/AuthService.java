package com.upb.agripos.service;

import com.upb.agripos.model.User;
import com.upb.agripos.exception.AuthenticationException;

/**
 * AuthService Interface - FR-5 Login & Role
 * 
 * Implementer: AuthServiceImpl
 * 
 * Handles:
 * - User login validation
 * - Role checking (KASIR / ADMIN)
 * - Session management (simple)
 * 
 * Created by: [Person D - Frontend/Auth]
 * Last modified: 
 */
public interface AuthService {

    /**
     * Login dengan username & password
     * 
     * @return User object jika login berhasil
     * @throws AuthenticationException jika login gagal
     */
    User login(String username, String password) throws AuthenticationException;

    /**
     * Get current logged-in user
     */
    User getCurrentUser();

    /**
     * Logout (clear session)
     */
    void logout();

    /**
     * Check apakah user saat ini adalah ADMIN
     */
    boolean isAdmin();

    /**
     * Check apakah user saat ini adalah KASIR
     */
    boolean isKasir();
}
