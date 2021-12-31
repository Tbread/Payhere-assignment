package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpResponseDto {
    private boolean success;
    private String message;
    private String email;

    @Builder
    public SignUpResponseDto(boolean success,String message,String email){
        this.email = email;
        this.message = message;
        this.success = success;
    }
}
