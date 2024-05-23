package com.example.BankTransfer.repository;

import com.example.BankTransfer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    Page<User> findByDateOfBirthAfter(Timestamp dateOfBirth, Pageable pageable);
    Page<User> findByPhone(String phone, Pageable pageable);
    Page<User> findByFullNameStartingWith(String fullName, Pageable pageable);
    Page<User> findByEmail(String email, Pageable pageable);
}
