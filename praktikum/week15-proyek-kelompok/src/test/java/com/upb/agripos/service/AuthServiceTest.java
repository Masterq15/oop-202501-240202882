package com.upb.agripos.service;

import com.upb.agripos.model.User;
import com.upb.agripos.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk AuthServiceImpl
 * 
 * Test cases:
 * 1. Login with valid KASIR credentials
 * 2. Login with valid ADMIN credentials
 * 3. Login with invalid username
 * 4. Login with invalid password
 * 5. Login with empty fields
 * 6. Session management (logout)
 * 7. Role checking
 */
public class AuthServiceTest {
    
    private AuthService authService;
    
    @BeforeEach
    public void setUp() {
        authService = new AuthServiceImpl();
    }
    
    @Test
    public void testLoginSuccessKasir() throws AuthenticationException {
        User user = authService.login("ismi", "password123");
        
        assertNotNull(user);
        assertEquals("ismi", user.getUsername());
        assertEquals("Ismi", user.getFullName());
        assertEquals(User.ROLE_KASIR, user.getRole());
        assertTrue(user.isKasir());
        assertFalse(user.isAdmin());
    }
    
    @Test
    public void testLoginSuccessAdmin() throws AuthenticationException {
        User user = authService.login("firly", "admin123");
        
        assertNotNull(user);
        assertEquals("firly", user.getUsername());
        assertEquals("Firly", user.getFullName());
        assertEquals(User.ROLE_ADMIN, user.getRole());
        assertTrue(user.isAdmin());
        assertFalse(user.isKasir());
    }
    
    @Test
    public void testLoginInvalidUsername() {
        assertThrows(AuthenticationException.class, () -> {
            authService.login("invalid_user", "password123");
        });
    }
    
    @Test
    public void testLoginInvalidPassword() {
        assertThrows(AuthenticationException.class, () -> {
            authService.login("ismi", "wrong_password");
        });
    }
    
    @Test
    public void testLoginEmptyUsername() {
        assertThrows(AuthenticationException.class, () -> {
            authService.login("", "password123");
        });
    }
    
    @Test
    public void testLoginEmptyPassword() {
        assertThrows(AuthenticationException.class, () -> {
            authService.login("ismi", "");
        });
    }
    
    @Test
    public void testGetCurrentUser() throws AuthenticationException {
        // Before login
        assertNull(authService.getCurrentUser());
        
        // After login
        User user = authService.login("ismi", "password123");
        assertEquals(user, authService.getCurrentUser());
    }
    
    @Test
    public void testLogout() throws AuthenticationException {
        authService.login("ismi", "password123");
        assertNotNull(authService.getCurrentUser());
        
        authService.logout();
        assertNull(authService.getCurrentUser());
    }
    
    @Test
    public void testIsAdminCheck() throws AuthenticationException {
        authService.login("firly", "admin123");
        assertTrue(authService.isAdmin());
        assertFalse(authService.isKasir());
    }
    
    @Test
    public void testIsKasirCheck() throws AuthenticationException {
        authService.login("ismi", "password123");
        assertTrue(authService.isKasir());
        assertFalse(authService.isAdmin());
    }
}
