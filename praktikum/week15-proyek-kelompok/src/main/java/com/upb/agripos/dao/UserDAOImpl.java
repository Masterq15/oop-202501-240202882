package com.upb.agripos.dao;

import com.upb.agripos.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAOImpl
 * Implementasi akses data untuk User menggunakan JDBC dan PostgreSQL
 */
public class UserDAOImpl implements UserDAO {
    private DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    @Override
    public List<User> findAll() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, role, active FROM users";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getBoolean("active")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public User findById(int id) throws Exception {
        String sql = "SELECT id, username, password, full_name, role, active FROM users WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getBoolean("active")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching user by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT id, username, password, full_name, role, active FROM users WHERE username = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getBoolean("active")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching user by username: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean save(User user) throws Exception {
        String sql = "INSERT INTO users (username, password, full_name, role, active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isActive());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) throws Exception {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, role = ?, active = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setInt(6, user.getId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            throw new Exception("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public User authenticate(String username, String password) throws Exception {
        User user = findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }
}
