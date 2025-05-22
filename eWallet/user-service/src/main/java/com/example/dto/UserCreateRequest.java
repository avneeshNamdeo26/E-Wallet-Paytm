package com.example.dto;

import com.example.UserIdentifier;
import com.example.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private UserIdentifier userIdentifier;

    @NotBlank
    private String identifierValue;

    public User toUser(){
        return User.builder()
                .name(name)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .userIdentifier(userIdentifier)
                .identifierValue(identifierValue)
                .build();
    }

}
