package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PageonceDeleteResponseDto {
    private boolean success;
    private String message;


    @Builder
    public PageonceDeleteResponseDto(boolean success,String message){
        this.success = success;
        this.message = message;
    }
}
