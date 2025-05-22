package com.example.controller;

import com.example.service.TransactionService;
import com.example.dto.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transact")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/initiate")
    public String transact(@RequestBody TransactionDTO transactionDTO) throws JsonProcessingException {
        System.out.println("API hitting");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return transactionService.transact(user.getUsername(),transactionDTO.getReceiver(),transactionDTO.getAmount()
                                    ,transactionDTO.getReason());
    }
}
