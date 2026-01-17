package com.upb.agripos.dao.impl;

import com.upb.agripos.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserDAOImpl - JDBC Implementation untuk User DAO
 * Person A - DATABASE MASTER
 * 
 * Implementasi Data Access Object untuk operasi CRUD User
 * Termasuk authentication dan role management
 */
public class UserDAOImpl {
    
    /**
     * Inner class untuk representasi User
     */
    public static class User {
        private int userId;
        private String username;
        private String password;
        private String fullName;
        private String role;
        private String email;
        private String phone;
        private boolean isActive;
        private Timestamp createdAt;
        private Timestamp lastLogin;
        
        // Constructor
        public User(int userId, String username, String password, String fullName, 
                   String role, String email, String phone, boolean isActive) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
            this.email = email;
            this.phone = phone;
            this.isActive = isActive;
        }
        
        public User() {}
        
        // Getters & Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public Timestamp getLastLogin() { return lastLogin; }
        public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
        
        @Override
        public String toString() {
            return "User{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", role='" + role + '\'' +
                    ", isActive=" + isActive +
                    '}';
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insert user baru ke database
     */
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role, email, phone, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setBoolean(7, user.isActive());
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error insert user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update user yang sudah ada
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, role=?, is_active=? " +
                     "WHERE user_id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());
            ps.setInt(6, user.getUserId());
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update password user
     */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Hapus user dari database (soft delete - set is_active = false)
     */
    public boolean delete(int userId) {
        String sql = "UPDATE users SET is_active=FALSE WHERE user_id=?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cari user berdasarkan ID
     */
    public User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find user by id: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cari user berdasarkan username (untuk login)
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find user by username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Authentikasi user dengan username dan password
     */
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            // Update last login timestamp
            updateLastLogin(user.getUserId());
            return user;
        }
        
        return null;
    }
    
    /**
     * Update last login timestamp
     */
    private boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update last login: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ambil semua user yang aktif
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY username";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error find all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Cari user berdasarkan role
     */
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND is_active = TRUE ORDER BY username";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, role);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error find users by role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Cek apakah username sudah terdaftar
     */
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error check username exists: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Hitung jumlah user berdasarkan role
     */
    public Map<String, Integer> getUserCountByRole() {
        Map<String, Integer> roleCount = new HashMap<>();
        String sql = "SELECT role, COUNT(*) as count FROM users WHERE is_active = TRUE GROUP BY role";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roleCount.put(rs.getString("role"), rs.getInt("count"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error get user count by role: " + e.getMessage());
            e.printStackTrace();
        }
        
        return roleCount;
    }
    
    /**
     * Ambil statistik user
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                     "COUNT(*) as total_users, " +
                     "SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active_users, " +
                     "SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) as admin_count, " +
                     "SUM(CASE WHEN role = 'MANAGER' THEN 1 ELSE 0 END) as manager_count, " +
                     "SUM(CASE WHEN role = 'CASHIER' THEN 1 ELSE 0 END) as cashier_count " +
                     "FROM users";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("total_users"));
                stats.put("activeUsers", rs.getInt("active_users"));
                stats.put("adminCount", rs.getInt("admin_count"));
                stats.put("managerCount", rs.getInt("manager_count"));
                stats.put("cashierCount", rs.getInt("cashier_count"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error get user statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Helper method untuk mapping ResultSet ke User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("role"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getBoolean("is_active")
        );
        
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        
        return user;
    }
}
