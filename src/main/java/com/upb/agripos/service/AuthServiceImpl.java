package com.upb.agripos.service;

import com.upb.agripos.model.User;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementasi Authentication Service dengan Database Integration
 * Person D - Frontend Week 15 + Person A - Database Master
 */
public class AuthServiceImpl implements IAuthService {
    
    private User currentUser;
    private com.upb.agripos.dao.impl.UserDAOImpl daoImpl;
    
    // Cache user di memory untuk performa
    private Map<String, User> userCache;
    
    public AuthServiceImpl() {
        this.currentUser = null;
        this.daoImpl = new com.upb.agripos.dao.impl.UserDAOImpl();
        this.userCache = new HashMap<>();
        loadUsersFromDatabase();
    }
    
    /**
     * Load semua users dari database ke memory cache
     */
    private void loadUsersFromDatabase() {
        System.out.println("Loading users dari database...");
        try {
            // Menggunakan findAll dari UserDAOImpl (mengembalikan List<User>)
            java.util.List<com.upb.agripos.dao.impl.UserDAOImpl.User> daoUsers = daoImpl.findAll();
            
            // Convert dari DAO User ke Model User
            this.userCache.clear();
            for (com.upb.agripos.dao.impl.UserDAOImpl.User daoUser : daoUsers) {
                User modelUser = new User(
                    String.valueOf(daoUser.getUserId()),
                    daoUser.getUsername(),
                    daoUser.getPassword(),
                    daoUser.getFullName(),
                    daoUser.getRole()
                );
                modelUser.setActive(daoUser.isActive());
                userCache.put(modelUser.getUsername(), modelUser);
            }
            System.out.println("✓ Loaded " + userCache.size() + " users dari database");
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            // Fallback: use dummy data if database fails
            initializeDummyData();
        }
    }
    
    /**
     * Initialize dummy user data untuk fallback
     */
    private void initializeDummyData() {
        System.out.println("⚠ Using dummy data (database connection failed)");
        // Kasir 1
        User kasir1 = new User("KSR001", "ismi", "password123", "Ismi", "KASIR");
        userCache.put("ismi", kasir1);
        
        // Admin
        User admin = new User("ADM001", "firly", "admin123", "Firly", "ADMIN");
        userCache.put("firly", admin);
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
        
        // Cek di cache
        User user = userCache.get(username);
        
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
        if (userCache.containsKey(user.getUsername())) {
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
        
        // Convert Model User ke DAO User dan simpan ke database
        try {
            com.upb.agripos.dao.impl.UserDAOImpl.User daoUser = new com.upb.agripos.dao.impl.UserDAOImpl.User(
                0, // userId auto generate
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getRole(),
                "", // email
                "", // phone
                user.isActive()
            );
            
            if (daoImpl.insert(daoUser)) {
                // Tambahkan ke cache juga
                userCache.put(user.getUsername(), user);
                System.out.println("✓ User baru terdaftar: " + user.getFullName());
                return true;
            } else {
                System.out.println("Error: Gagal menyimpan user ke database");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        return userCache.get(username);
    }
    
    /**
     * Helper: Get semua users (untuk admin management)
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(userCache);
    }
    
    /**
     * Helper: Reload users dari database (setelah ada perubahan)
     */
    public void reloadUsersFromDatabase() {
        loadUsersFromDatabase();
    }
    
    /**
     * Helper: Deactivate user (untuk admin)
     */
    public boolean deactivateUser(String username) {
        User user = userCache.get(username);
        if (user != null) {
            user.setActive(false);
            try {
                com.upb.agripos.dao.impl.UserDAOImpl.User daoUser = new com.upb.agripos.dao.impl.UserDAOImpl.User(
                    0,
                    user.getUsername(),
                    user.getPassword(),
                    user.getFullName(),
                    user.getRole(),
                    "",
                    "",
                    user.isActive()
                );
                daoImpl.update(daoUser);
            } catch (Exception e) {
                System.err.println("Error deactivating user: " + e.getMessage());
            }
            System.out.println("✓ User " + user.getFullName() + " sudah dinonaktifkan");
            return true;
        }
        return false;
    }
}
