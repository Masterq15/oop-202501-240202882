package com.upb.agripos.service;

import java.util.List;

import com.upb.agripos.dao.UserDAO;
import com.upb.agripos.dao.UserDAOImpl;
import com.upb.agripos.model.User;

/**
 * UserService
 * Layer bisnis untuk manajemen user
 * Mengenkapsulasi logika bisnis terkait user management
 */
public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Get semua user
     */
    public List<User> getAllUsers() throws Exception {
        return userDAO.findAll();
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) throws Exception {
        return userDAO.findById(id);
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) throws Exception {
        return userDAO.findByUsername(username);
    }

    /**
     * Tambah user baru
     */
    public boolean createUser(String username, String password, String fullName, String role) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap tidak boleh kosong");
        }

        // Cek username sudah ada
        User existingUser = userDAO.findByUsername(username);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username sudah terdaftar");
        }

        User newUser = new User(0, username, password, fullName, role, true);
        return userDAO.save(newUser);
    }

    /**
     * Update user
     */
    public boolean updateUser(int id, String password, String fullName, boolean active) throws Exception {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }

        user.setPassword(password);
        user.setFullName(fullName);
        user.setActive(active);

        return userDAO.update(user);
    }

    /**
     * Delete user
     */
    public boolean deleteUser(int id) throws Exception {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User tidak ditemukan");
        }

        // Jangan allow delete user yang login
        return userDAO.delete(id);
    }

    /**
     * Authenticate user
     */
    public User authenticate(String username, String password) throws Exception {
        return userDAO.authenticate(username, password);
    }
}
