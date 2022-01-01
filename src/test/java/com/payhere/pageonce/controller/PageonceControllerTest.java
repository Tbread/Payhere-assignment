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
    private Pageonce testPageonce4;


    @BeforeEach
    public void setup() {
        testUser = new User("test@test.com", passwordEncoder.encode("abc"));
        userRepository.save(testUser);
        token = jwtTokenProvider.createToken(testUser.getId().toString(), "test@test.com");
        testPageonce = Pageonce.builder().expenditure(500L).memo("testMemo").userId(testUser.getId()).build();
        testPageonce2 = new Pageonce("testMemo2", 200L, testUser.getId(), true);
        testPageonce3 = new Pageonce("testMemo3", 300L, 99999999L);
        testPageonce4 = new Pageonce("testMemo4",400L,999999999L,true);
        pageonceRepository.save(testPageonce);
        pageonceRepository.save(testPageonce2);
        pageonceRepository.save(testPageonce3);
        pageonceRepository.save(testPageonce4);
    }

    @Test
    @DisplayName("01.가계부작성-정상1")
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
    @DisplayName("02.가계부작성-정상2,메모누락")
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
    @DisplayName("03.가계부작성실패-비로그인")
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
    @DisplayName("04.가계부작성실패-지출누락")
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
    @DisplayName("05.가계부상세조회-성공")
    public void detailsSuccess() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();

        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}", pageonceId)
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
    @DisplayName("06.가계부상세조회실패-잘못된id")
    public void detailsFailWrongId() throws Exception {
        //given
        Long pageonceId = 99999999999999L;


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}", pageonceId)
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
    @DisplayName("07.가계부상세조회실패-삭제된가계부")
    public void detailsFailDeletedPageonce() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}", pageonceId)
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
    @DisplayName("08.가계부상세조회실패-다른유저의가계부")
    public void detailsFailNotOwner() throws Exception {
        //given
        Long pageonceId = testPageonce3.getId();


        //when
        ResultActions resultActions = mockMvc.perform(get("/view/{id}", pageonceId)
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

    @Test
    @DisplayName("09.가계부수정-정상1")
    public void modifySuccess() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, "expectSuccess");


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 수정하였습니다."))
                .andExpect(jsonPath("memo").value("expectSuccess"))
                .andExpect(jsonPath("expenditure").value(8000L));
    }

    @Test
    @DisplayName("10.가계부수정-정상2,메모누락")
    public void modifySuccessNullMemo() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, null);


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 수정하였습니다."))
                .andExpect(jsonPath("memo").isEmpty())
                .andExpect(jsonPath("expenditure").value(8000L));
    }

    @Test
    @DisplayName("11.가계부수정-실패1,비로그인유저")
    public void modifyFailNoAuth() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, null);


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("12.가계부수정-실패2,잘못된 ID")
    public void modifyFailWrongId() throws Exception {
        //given
        Long pageonceId = 99999L;
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, "expectFail");


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다."))
                .andExpect(jsonPath("expenditure").isEmpty())
                .andExpect(jsonPath("memo").isEmpty());
    }

    @Test
    @DisplayName("13.가계부수정-실패3,삭제된 가계부")
    public void modifyFailDeletedId() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, "expectFail");


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다."))
                .andExpect(jsonPath("expenditure").isEmpty())
                .andExpect(jsonPath("memo").isEmpty());
    }

    @Test
    @DisplayName("14.가계부수정-실패4,타인의 가계부")
    public void modifyFailNotOwner() throws Exception {
        //given
        Long pageonceId = testPageonce3.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(8000L, "expectFail");


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("자신의 가계부만 수정할 수 있습니다."))
                .andExpect(jsonPath("expenditure").isEmpty())
                .andExpect(jsonPath("memo").isEmpty());
    }

    @Test
    @DisplayName("15.가계부수정-실패5,금액 누락")
    public void modifyFailNullExpenditure() throws Exception {
        //given
        Long pageonceId = testPageonce3.getId();
        PageonceWriteRequestDto pageonceWriteRequestDto = new PageonceWriteRequestDto(null, "expectFail");


        //when
        ResultActions resultActions = mockMvc.perform(patch("/modify/{id}", pageonceId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageonceWriteRequestDto))
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("지출금액은 필수 값입니다"))
                .andExpect(jsonPath("expenditure").isEmpty())
                .andExpect(jsonPath("memo").isEmpty());
    }

    @Test
    @DisplayName("16.가계부삭제-정상")
    public void deleteSuccess() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/delete/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 삭제하였습니다."));
    }

    @Test
    @DisplayName("17.가계부삭제-실패1,비로그인")
    public void deleteFailNoAuth() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/delete/{id}", pageonceId)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("18.가계부삭제-실패2,잘못된 ID")
    public void deleteFailWrongId() throws Exception {
        //given
        Long pageonceId = 9999999999L;


        //when
        ResultActions resultActions = mockMvc.perform(patch("/delete/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 이미 삭제된 가계부입니다."));
    }

    @Test
    @DisplayName("19.가계부삭제-실패2,삭제된 가계부")
    public void deleteFailDeleted() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/delete/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 가계부가 존재하지 않거나 이미 삭제된 가계부입니다."));
    }

    @Test
    @DisplayName("20.가계부삭제-실패2,타인의 가계부")
    public void deleteFailNotOwner() throws Exception {
        //given
        Long pageonceId = testPageonce3.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/delete/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("자신의 가계부만 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("21.가계부복원-정상")
    public void restoreSuccess() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/restore/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").value("성공적으로 복구하였습니다."));
    }

    @Test
    @DisplayName("22.가계부복원실패-비로그인")
    public void restoreFailNoAuth() throws Exception {
        //given
        Long pageonceId = testPageonce2.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/restore/{id}", pageonceId)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("23.가계부복원실패-삭제되지않은 가계부")
    public void restoreFailNotDeleted() throws Exception {
        //given
        Long pageonceId = testPageonce.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/restore/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("해당 ID의 삭제된 가계부가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("24.가계부복원실패-타인의 가계부")
    public void restoreFailNotOwner() throws Exception {
        //given
        Long pageonceId = testPageonce4.getId();


        //when
        ResultActions resultActions = mockMvc.perform(patch("/restore/{id}", pageonceId)
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)).andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(false))
                .andExpect(jsonPath("message").value("자신의 가계부만 복구할 수 있습니다."));
    }

}