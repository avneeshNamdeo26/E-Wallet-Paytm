package com.example.service;

import com.example.model.User;
import com.example.dto.UserCreateRequest;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.CommonConstants.*;
import static com.example.constants.UserConstants.USER_AUTHORITY;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void create(UserCreateRequest userCreateRequest) throws JsonProcessingException {
        User user = userCreateRequest.toUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities(USER_AUTHORITY);
        user = userRepository.save(user);

        //Publish the event post user creation which will be listed by customers
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USER_CREATED_TOPIC_USERID,user.getId());
        jsonObject.put(USER_CREATED_TOPIC_PHONE_NUMBER,user.getPhoneNumber());
        jsonObject.put(USER_CREATED_TOPIC_IDENTIFIER_KEY,user.getUserIdentifier());
        jsonObject.put(USER_CREATED_TOPIC_IDENTIFIER_VALUE,user.getIdentifierValue());
        jsonObject.put(USER_CREATED_TOPIC_EMAIL,user.getEmail());

        kafkaTemplate.send(USER_CREATED_TOPIC,objectMapper.writeValueAsString(jsonObject));
    }



    public List<User> getAllUserDetails() {
        return userRepository.findAll();
    }
}
