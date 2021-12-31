package com.payhere.pageonce.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class PageonceDetailsResponseDto {
    private boolean success;
    private String message;
    private String memo;
    private Long expenditure;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public PageonceDetailsResponseDto(boolean success, String message, String memo, Long expenditure,LocalDateTime createdAt,LocalDateTime modifiedAt) {
        this.success = success;
        this.message = message;
        this.memo = memo;
        this.expenditure = expenditure;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
