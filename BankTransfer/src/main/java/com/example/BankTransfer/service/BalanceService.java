package com.example.BankTransfer.service;

import com.example.BankTransfer.model.User;
import com.example.BankTransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void applyInterest() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            double initialBalance = user.getBankAccount().getInitialBalance();
            double currentBalance = user.getBankAccount().getBalance();
            double maxBalance = initialBalance * 2.07;
            if (currentBalance < maxBalance) {
                double newBalance = currentBalance * 1.05;
                user.getBankAccount().setBalance(Math.min(newBalance, maxBalance));
                userRepository.save(user);
            }
        }
    }
}
