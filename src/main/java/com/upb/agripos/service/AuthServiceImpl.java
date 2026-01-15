package com.upb.agripos.service;

import com.upb.agripos.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementasi Authentication Service
 * Untuk simulasi, menggunakan dummy data. Nanti akan replace dengan database
 * Person D - Frontend Week 15
 */
public class AuthServiceImpl implements IAuthService {
    
    private User currentUser;
    
    // Dummy data - nanti ganti dengan database
    private Map<String, User> userDatabase;
    
    public AuthServiceImpl() {
        this.currentUser = null;
        this.userDatabase = new HashMap<>();
        initializeDummyData();
    }
    
    /**
     * Initialize dummy user data untuk testing
     * Nanti akan replace dengan query dari database (Person A)
     */
    private void initializeDummyData() {
        // Kasir 1
        User kasir1 = new User("KSR001", "ismi", "password123", "Ismi", "KASIR");
        userDatabase.put("ismi", kasir1);
        
        // Admin
        User admin = new User("ADM001", "firly", "admin123", "Firly", "ADMIN");
        userDatabase.put("firly", admin);
    }
    
    @Override
    public User login(String username, String password) {
        // Validasi input
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username tidak boleh kosong");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: Password tidak boleh kosong");
            return null;
        }
        
        // Cek di database dummy
        User user = userDatabase.get(username);
        
        if (user == null) {
            System.out.println("Error: User tidak ditemukan - " + username);
            return null;
        }
        
        // Validasi password (plaintext untuk simulasi)
        if (!user.getPassword().equals(password)) {
            System.out.println("Error: Password salah");
            return null;
        }
        
        // Validasi user aktif
        if (!user.isActive()) {
            System.out.println("Error: User tidak aktif");
            return null;
        }
        
        // Set current user (login berhasil)
        this.currentUser = user;
        System.out.println("✓ Login berhasil: " + user.getFullName() + " (" + user.getRole() + ")");
        return user;
    }
    
    @Override
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logout: " + currentUser.getFullName());
            this.currentUser = null;
        }
    }
    
    @Override
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    @Override
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }
    
    @Override
    public boolean registerUser(User user) {
        // Hanya admin yang bisa register
        if (currentUser == null || !currentUser.isAdmin()) {
            System.out.println("Error: Hanya admin yang bisa register user");
            return false;
        }
        
        // Validasi username belum ada
        if (userDatabase.containsKey(user.getUsername())) {
            System.out.println("Error: Username sudah terdaftar");
            return false;
        }
        
        // Validasi input
        if (!isValidUsername(user.getUsername())) {
            System.out.println("Error: Username tidak valid (min 3 karakter)");
            return false;
        }
        
        if (!isValidPassword(user.getPassword())) {
            System.out.println("Error: Password tidak valid (min 6 karakter)");
            return false;
        }
        
        // Tambahkan user baru
        userDatabase.put(user.getUsername(), user);
        System.out.println("✓ User baru terdaftar: " + user.getFullName());
        return true;
    }
    
    @Override
    public boolean isValidUsername(String username) {
        // Min 3 karakter, alphanumeric
        return username != null && username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }
    
    @Override
    public boolean isValidPassword(String password) {
        // Min 6 karakter
        return password != null && password.length() >= 6;
    }
    
    /**
     * Helper: Get user by username (untuk admin management)
     */
    public User getUserByUsername(String username) {
        return userDatabase.get(username);
    }
    
    /**
     * Helper: Get semua users (untuk admin management)
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(userDatabase);
    }
    
    /**
     * Helper: Deactivate user (untuk admin)
     */
    public boolean deactivateUser(String username) {
        User user = userDatabase.get(username);
        if (user != null) {
            user.setActive(false);
            System.out.println("✓ User " + user.getFullName() + " sudah dinonaktifkan");
            return true;
        }
        return false;
    }
}
