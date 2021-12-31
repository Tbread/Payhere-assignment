package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PageonceWriteResponseDto {
    private boolean success;
    private String message;
    private String memo;
    private Long expenditure;

    @Builder
    public PageonceWriteResponseDto(boolean success,String message,String memo,Long expenditure){
        this.success = success;
        this.message = message;
        this.memo = memo;
        this.expenditure = expenditure;
    }
}
