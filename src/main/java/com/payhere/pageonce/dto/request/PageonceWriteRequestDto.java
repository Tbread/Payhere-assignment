package com.payhere.pageonce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PageonceWriteRequestDto {

    @NotNull(message = "지출금액은 필수 값입니다")
    private Long expenditure;

    private String memo;
}
