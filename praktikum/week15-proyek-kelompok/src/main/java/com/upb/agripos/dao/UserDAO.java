package com.upb.agripos.dao;

import com.upb.agripos.model.User;
import com.upb.agripos.exception.DatabaseException;
import java.util.List;

/**
 * UserDAO Interface - Abstraction untuk User CRUD
 * Untuk FR-5 (Login & Role)
 * 
 * Implementer: UserDAOImpl (JDBC + PostgreSQL)
 * 
 * Created by: [Person D - Frontend/Auth]
 * Last modified: 
 */
public interface UserDAO {

    /**
     * Tambah user baru
     */
    boolean insert(User user) throws DatabaseException;

    /**
     * Cari user by userId
     */
    User findById(String userId) throws DatabaseException;

    /**
     * Cari user by username (untuk login)
     */
    User findByUsername(String username) throws DatabaseException;

    /**
     * Cari semua user
     */
    List<User> findAll() throws DatabaseException;

    /**
     * Update user
     */
    boolean update(User user) throws DatabaseException;

    /**
     * Hapus user by userId
     */
    boolean delete(String userId) throws DatabaseException;
}
