package com.payhere.pageonce.controller;

import com.payhere.pageonce.BaseIntegrationTest;
import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.jwt.JwtTokenProvider;
import com.payhere.pageonce.model.Pageonce;
import com.payhere.pageonce.model.User;
import com.payhere.pageonce.repository.PageonceRepository;
import com.payhere.pageonce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @Autowired
    PageonceRepository pageonceRepository;

    private User testUser;
    private String token;
    private Pageonce testPageonce;
    private Pageonce testPageonce2;
    private Pageonce testPageonce3;


    @BeforeEach
    public void setup() {
        testUser = new User("test@test.com", passwordEncoder.encode("abc"));
        userRepository.save(testUser);
        token = jwtTokenProvider.createToken(testUser.getId().toString(), "test@test.com");
        testPageonce = Pageonce.builder().expenditure(500L).memo("testMemo").userId(testUser.getId()).build();
        testPageonce2 = new Pageonce("testMemo2",200L,testUser.getId(),true);
        testPageonce3 = new Pageonce("testMemo3",300L,99999999L);
        pageonceRepository.save(testPageonce);
        pageonceRepository.save(testPageonce2);
        pageonceRepository.save(testPageonce3);
    }

    @Test
    @DisplayName("가계부작성-정상1")
    public void writeSuccess1() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(1000L, "expectSuccess");


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("정상적으로 가계부가 작성되었습니다."))
                .andExpect(jsonPath("memo").value("expectSuccess"))
                .andExpect(jsonPath("expenditure").value(1000L));
    }

    @Test
    @DisplayName("가계부작성-정상2,메모누락")
    public void writeSuccess2() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(1000L, null);


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .header("Authorization", token)
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

    @Test
    @DisplayName("가계부작성실패-비로그인")
    public void writeFailNoAuth() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(1000L, "expect403");


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("가계부작성실패-지출누락")
    public void writeFailNullExpenditure() throws Exception {
        //given
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(null, "expectReqNotNull");


        //when
        ResultActions resultActions = mockMvc.perform(post("/write")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("지출금액은 필수 값입니다"))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").isEmpty());
    }

    @Test
    @DisplayName("가계부상세조회-성공")
    public void detailsSuccess() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();

        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}",pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 가계부를 불러왔습니다."))
                .andExpect(jsonPath("memo").value("testMemo"))
                .andExpect(jsonPath("expenditure").value(500L));
    }

    @Test
    @DisplayName("가계부상세조회실패-잘못된id")
    public void detailsFailWrongId() throws Exception {
        //given
        Long pageonceId = 99999999999999L;


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}",pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다."))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").isEmpty());
    }

    @Test
    @DisplayName("가계부상세조회실패-삭제된가계부")
    public void detailsFailDeletedPageonce() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}",pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다."))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").isEmpty());
    }

    @Test
    @DisplayName("가계부상세조회실패-다른유저의가계부")
    public void detailsFailNotOwner() throws Exception {
        //given
        Long pageonceId = testPageonce3.getId();
        System.out.println(testUser.getId());
        System.out.println(testPageonce3.getUserId());


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}",pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("자신의 가계부만 조회할 수 있습니다"))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").isEmpty());
    }


}