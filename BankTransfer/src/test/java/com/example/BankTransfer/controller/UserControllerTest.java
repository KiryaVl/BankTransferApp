package com.example.BankTransfer.controller;

import com.example.BankTransfer.model.User;
import com.example.BankTransfer.service.TransactionService;
import com.example.BankTransfer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        User user = new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0);
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateContactInfo_Success() throws Exception {
        User user = new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0);
        when(userService.updateContactInfo(anyLong(), any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateContactInfo(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testSearchUsers_ByBirthDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2000-01-01");
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Collections.singletonList(new User("testuser", "password", "1234567890", "test@example.com", new Date(), "Test User", 100.0));
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userService.searchUsers(anyString(), anyString(), anyString(), anyString(), any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<Page<User>> result = userController.searchUsers("2000-01-01", null, null, null, pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotalElements());
        assertEquals("testuser", result.getBody().getContent().get(0).getUsername());
    }

    @Test
    void testTransferFunds_Success() {
        ResponseEntity<String> response = userController.transferFunds(1L, 2L, 50.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transfer successful", response.getBody());
    }
}
