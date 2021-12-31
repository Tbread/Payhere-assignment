package com.payhere.pageonce.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}
