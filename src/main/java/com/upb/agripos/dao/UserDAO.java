package com.upb.agripos.dao;

import com.upb.agripos.model.User;
import java.util.List;
import java.util.Map;

/**
 * UserDAO Interface untuk operasi CRUD User
 * Person A - DATABASE MASTER
 */
public interface UserDAO {
    
    /**
     * Insert user baru ke database
     */
    boolean insert(User user);
    
    /**
     * Update user yang sudah ada
     */
    boolean update(User user);
    
    /**
     * Delete user berdasarkan username
     */
    boolean delete(String username);
    
    /**
     * Get user berdasarkan username
     */
    User getUserByUsername(String username);
    
    /**
     * Get user berdasarkan userId
     */
    User getUserById(String userId);
    
    /**
     * Get semua users
     */
    Map<String, User> getAllUsers();
    
    /**
     * Check apakah username sudah ada
     */
    boolean isUsernameExists(String username);
}
