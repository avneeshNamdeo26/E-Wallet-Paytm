package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
@SpringBootApplication
public class UserApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        User serviceUser = User.builder()
//                .phoneNumber("service")
//                .password(passwordEncoder.encode("serv123"))
//                .authorities(UserConstants.SERVICE_AUTHORITY)
//                .email("no-reply@company.com")
//                .userIdentifier(UserIdentifier.SERVICE_ID)
//                .identifierValue("serv123")
//                .build();
//        userRepository.save(serviceUser);
    }
}