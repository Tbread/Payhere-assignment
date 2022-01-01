package com.payhere.pageonce.controller;

import com.payhere.pageonce.dto.request.LoginRequestDto;
import com.payhere.pageonce.dto.request.SignUpRequestDto;
import com.payhere.pageonce.dto.response.LoginResponseDto;
import com.payhere.pageonce.dto.response.SignUpResponseDto;
import com.payhere.pageonce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public SignUpResponseDto register(@RequestBody @Valid SignUpRequestDto signUpRequestDto, BindingResult bindingResult){
        return userService.register(signUpRequestDto,bindingResult);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto, BindingResult bindingResult){
        return userService.login(loginRequestDto,bindingResult);
    }
}
