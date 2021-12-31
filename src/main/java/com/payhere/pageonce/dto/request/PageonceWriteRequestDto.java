package com.payhere.pageonce.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
public class PageonceWriteRequestDto {

    @NotEmpty
    private Long expenditure;

    private String memo;
}
