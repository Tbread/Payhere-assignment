package com.payhere.pageonce.controller;

import com.payhere.pageonce.dto.request.SignUpRequestDto;
import com.payhere.pageonce.dto.response.SignUpResponseDto;
import com.payhere.pageonce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public SignUpResponseDto register(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        return userService.register(signUpRequestDto);
    }

}
