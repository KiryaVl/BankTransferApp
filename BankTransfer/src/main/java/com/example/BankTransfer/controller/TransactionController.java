package com.example.BankTransfer.controller;

import com.example.BankTransfer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestParam Long fromUserId, @RequestParam Long toUserId, @RequestParam double amount) {
        try {
            transactionService.transferFunds(fromUserId, toUserId, amount);
            return new ResponseEntity<>("Transfer successful.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
