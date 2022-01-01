package com.payhere.pageonce.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
public class PageonceWriteRequestDto {

    @NotEmpty(message = "지출금액은 필수 값입니다")
    private Long expenditure;

    private String memo;
}
