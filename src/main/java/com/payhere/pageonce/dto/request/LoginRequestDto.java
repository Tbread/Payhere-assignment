package com.payhere.pageonce.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
public class LoginRequestDto {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
