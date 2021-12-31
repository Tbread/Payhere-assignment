package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SimpleResponseDto {
    private boolean success;
    private String message;


    @Builder
    public SimpleResponseDto(boolean success, String message){
        this.success = success;
        this.message = message;
    }
}
