package com.upb.agripos.dao;

import com.upb.agripos.model.User;

import java.util.List;

/**
 * UserDAO Interface
 * Mendefinisikan kontrak untuk akses data pengguna
 */
public interface UserDAO {
    List<User> findAll() throws Exception;
    User findById(int id) throws Exception;
    User findByUsername(String username) throws Exception;
    boolean save(User user) throws Exception;
    boolean update(User user) throws Exception;
    boolean delete(int id) throws Exception;
    User authenticate(String username, String password) throws Exception;
}
