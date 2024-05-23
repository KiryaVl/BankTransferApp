package com.example.BankTransfer.service;

import com.example.BankTransfer.model.BankAccount;
import com.example.BankTransfer.model.User;
import com.example.BankTransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) throws Exception {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username is already taken.");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new Exception("Phone is already taken.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email is already taken.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Create and set the bank account
        BankAccount bankAccount = new BankAccount(user.getBankAccount().getInitialBalance());
        user.setBankAccount(bankAccount);

        return userRepository.save(user);
    }

    public User updateContactInfo(Long id, User user) throws Exception {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            if (user.getPhone() != null && !user.getPhone().isEmpty() && !user.getPhone().equals(updatedUser.getPhone())) {
                if (userRepository.existsByPhone(user.getPhone())) {
                    throw new Exception("Phone is already taken.");
                }
                updatedUser.setPhone(user.getPhone());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().equals(updatedUser.getEmail())) {
                if (userRepository.existsByEmail(user.getEmail())) {
                    throw new Exception("Email is already taken.");
                }
                updatedUser.setEmail(user.getEmail());
            }
            return userRepository.save(updatedUser);

        } else {
            throw new Exception("User not found.");
        }
    }

    public Page<User> searchUsers(String birthDate, String phone, String fullName, String email, Pageable pageable) throws ParseException {
        if (birthDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(birthDate);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return userRepository.findByDateOfBirthAfter(timestamp, pageable);
        }
        if (phone != null) {
            return userRepository.findByPhone(phone, pageable);
        }
        if (fullName != null) {
            return userRepository.findByFullNameStartingWith(fullName, pageable);
        }
        if (email != null) {
            return userRepository.findByEmail(email, pageable);
        }
        return userRepository.findAll(pageable);
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void updateBalances() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            BankAccount bankAccount = user.getBankAccount();
            double initialBalance = bankAccount.getInitialBalance();
            double currentBalance = bankAccount.getBalance();
            if (currentBalance < initialBalance * 2.07) {
                double newBalance = currentBalance * 1.05;
                if (newBalance > initialBalance * 2.07) {
                    newBalance = initialBalance * 2.07;
                }
                bankAccount.setBalance(newBalance);
                userRepository.save(user);
            }
        }
    }
}
