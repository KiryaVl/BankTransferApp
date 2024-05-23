package com.example.BankTransfer.service;

import com.example.BankTransfer.model.BankAccount;
import com.example.BankTransfer.model.User;
import com.example.BankTransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void transferFunds(Long fromUserId, Long toUserId, double amount) throws Exception {
        Optional<User> fromUserOptional = userRepository.findById(fromUserId);
        Optional<User> toUserOptional = userRepository.findById(toUserId);

        if (fromUserOptional.isPresent() && toUserOptional.isPresent()) {
            User fromUser = fromUserOptional.get();
            User toUser = toUserOptional.get();
            BankAccount fromAccount = fromUser.getBankAccount();
            BankAccount toAccount = toUser.getBankAccount();

            if (fromAccount.getBalance() >= amount) {
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);

                userRepository.save(fromUser);
                userRepository.save(toUser);
            } else {
                throw new Exception("Insufficient funds.");
            }
        } else {
            throw new Exception("User not found.");
        }
    }
}
