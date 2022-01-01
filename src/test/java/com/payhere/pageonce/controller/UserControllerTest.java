package com.payhere.pageonce.controller;

import com.payhere.pageonce.BaseIntegrationTest;
import com.payhere.pageonce.dto.request.LoginRequestDto;
import com.payhere.pageonce.dto.request.SignUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("test@email.com", "abc");
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());
    }

    @Test
    @DisplayName("회원가입-정상")
    public void registerSuccess() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("test@test.com", "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("email").value("test@test.com"))
                .andExpect(jsonPath("message").value("성공적으로 가입되었습니다."));
    }

    @Test
    @DisplayName("회원가입실패-이메일누락")
    public void registerFailNullEmail() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(null, "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("이메일은 필수 입력 값입니다."))
                .andExpect(jsonPath("email").isEmpty());

    }

    @Test
    @DisplayName("회원가입실패-패스워드누락")
    public void registerFailNullPassword() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("test@test.com", null);

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("비밀번호는 필수 입력 값입니다."))
                .andExpect(jsonPath("email").isEmpty());

    }

    @Test
    @DisplayName("회원가입실패-이메일형식")
    public void registerFailValidEmail() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("test", "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("이메일형식이 잘못되었습니다."))
                .andExpect(jsonPath("email").isEmpty());

    }

    @Test
    @DisplayName("회원가입실패-모두누락")
    public void registerFailNull() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(null, null);

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("email").isEmpty());

    }

    @Test
    @DisplayName("회원가입실패-중복이메일")
    public void registerFailDupEmail() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("test@email.com", "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("이미 존재하는 이메일입니다."))
                .andExpect(jsonPath("email").isEmpty());

    }

    @Test
    @DisplayName("로그인-정상")
    public void loginSuccess() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 로그인 되었습니다."))
                .andExpect(jsonPath("email").value("test@email.com"))
                .andExpect(jsonPath("token").isNotEmpty());

    }

    @Test
    @DisplayName("로그인실패-이메일누락")
    public void loginFailNullEmail() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto(null, "abc");

        //when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("이메일은 필수 입력 값입니다."))
                .andExpect(jsonPath("email").isEmpty())
                .andExpect(jsonPath("token").isEmpty());

    }

    @Test
    @DisplayName("로그인실패-패스워드누락")
    public void loginFailNullPassword() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@email.com", null);

        //when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("패스워드는 필수 입력 값입니다."))
                .andExpect(jsonPath("email").isEmpty())
                .andExpect(jsonPath("token").isEmpty());

    }

    @Test
    @DisplayName("로그인실패-모두누락")
    public void loginFailNull() throws Exception {
        //given
        LoginRequestDto loginRequestDto = new LoginRequestDto(null, null);

        //when
        ResultActions resultActions = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("email").isEmpty())
                .andExpect(jsonPath("token").isEmpty());

    }



}