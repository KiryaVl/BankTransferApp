package com.example.BankTransfer.service;

import com.example.BankTransfer.model.User;
import com.example.BankTransfer.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        User user = new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByPhone(user.getPhone())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals("testuser", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("1234567890", createdUser.getPhone());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("Test User", createdUser.getFullName());
        assertEquals(100.0, createdUser.getBankAccount().getInitialBalance());
        assertEquals(100.0, createdUser.getBankAccount().getBalance());
    }

    @Test
    void testUpdateContactInfo_Success() throws Exception {
        User existingUser = new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0);
        User updateUser = new User();
        updateUser.setPhone("0987654321");
        updateUser.setEmail("new@example.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByPhone(updateUser.getPhone())).thenReturn(false);
        when(userRepository.existsByEmail(updateUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateContactInfo(1L, updateUser);

        assertEquals("0987654321", updatedUser.getPhone());
        assertEquals("new@example.com", updatedUser.getEmail());
    }
    @Test
    void testSearchUsers_ByBirthDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2000-01-01");
        Timestamp timestamp = new Timestamp(date.getTime());
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0));
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findByDateOfBirthAfter(any(Timestamp.class), any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.searchUsers("2000-01-01", null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testUpdateBalances() {
        User user1 = new User("user1", "password", "1234567890", "user1@example.com", new Date(), "User One", 100.0);
        user1.getBankAccount().setBalance(100.0);
        User user2 = new User("user2", "password", "0987654321", "user2@example.com", new Date(), "User Two", 100.0);
        user2.getBankAccount().setBalance(150.0);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        userService.updateBalances();

        assertEquals(105.0, user1.getBankAccount().getBalance());
        assertEquals(157.5, user2.getBankAccount().getBalance());
    }
}
