package com.example.BankTransfer.service;

import com.example.BankTransfer.model.BankAccount;
import com.example.BankTransfer.model.User;
import com.example.BankTransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferFunds_Success() throws Exception {
        User fromUser = new User("fromUser", "password", "1234567890", "from@example.com", new Date(), "From User", 100.0);
        fromUser.getBankAccount().setBalance(200.0);
        User toUser = new User("toUser", "password", "0987654321", "to@example.com", new Date(), "To User", 100.0);
        toUser.getBankAccount().setBalance(100.0);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(fromUser)).thenReturn(Optional.of(toUser));
        when(userRepository.save(any(User.class))).thenReturn(fromUser).thenReturn(toUser);

        transactionService.transferFunds(1L, 2L, 50.0);

        assertEquals(150.0, fromUser.getBankAccount().getBalance());
        assertEquals(150.0, toUser.getBankAccount().getBalance());
    }

    @Test
    void testTransferFunds_InsufficientFunds() {
        User fromUser = new User("fromUser", "password", "1234567890", "from@example.com", new Date(), "From User", 100.0);
        fromUser.getBankAccount().setBalance(50.0);
        User toUser = new User("toUser", "password", "0987654321", "to@example.com", new Date(), "To User", 100.0);
        toUser.getBankAccount().setBalance(100.0);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(fromUser)).thenReturn(Optional.of(toUser));

        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.transferFunds(1L, 2L, 100.0);
        });

        assertEquals("Insufficient funds.", exception.getMessage());
    }
}
