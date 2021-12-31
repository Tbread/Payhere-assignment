package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginResponseDto {

    private boolean success;
    private String message;
    private String email;
    private String token;

    @Builder
    public LoginResponseDto(boolean success,String message,String email,String token){
        this.email = email;
        this.message = message;
        this.success = success;
        this.token = token;
    }
}
