package com.payhere.pageonce.controller;

import com.payhere.pageonce.BaseIntegrationTest;
import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.jwt.JwtTokenProvider;
import com.payhere.pageonce.model.User;
import com.payhere.pageonce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;


import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PageonceControllerTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    PasswordEncoder passwordEncoder;

    private User testUser;
    private String token;


    @BeforeEach
    public void setup() {
        testUser = new User("test@test.com",passwordEncoder.encode("abc"));
        userRepository.save(testUser);
        token = jwtTokenProvider.createToken(testUser.getId().toString(),"test@test.com");
    }

    @Test
    @DisplayName("가계부작성-정상1")
    public void writeSuccess1() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(1000L, "successCase");


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("정상적으로 가계부가 작성되었습니다."))
                .andExpect(jsonPath("memo").value("successCase"))
                .andExpect(jsonPath("expenditure").value(1000L));
    }

    @Test
    @DisplayName("가계부작성-정상2,메모누락")
    public void writeSuccess2() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(1000L, null);


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("정상적으로 가계부가 작성되었습니다."))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").value(1000L));
    }


}