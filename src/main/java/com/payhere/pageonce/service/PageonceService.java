package com.payhere.pageonce.service;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.dto.response.PageonceViewResponseDto;
import com.payhere.pageonce.dto.response.SimpleResponseDto;
import com.payhere.pageonce.dto.response.PageonceDetailsResponseDto;
import com.payhere.pageonce.dto.response.PageonceWriteResponseDto;
import com.payhere.pageonce.model.Pageonce;
import com.payhere.pageonce.model.PageonceView;
import com.payhere.pageonce.repository.PageonceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageonceService {
    private final PageonceRepository pageonceRepository;


    //가계부 작성
    @Transactional
    public PageonceWriteResponseDto write(UserDetailsImpl userDetails, PageonceWriteRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return PageonceWriteResponseDto.builder()
                    .success(false)
                    .message(message)
                    .build();
        }
        Long userId = userDetails.getUser().getId();
        Pageonce pageonce = Pageonce.builder()
                .expenditure(requestDto.getExpenditure())
                .memo(requestDto.getMemo())
                .userId(userId)
                .build();
        pageonceRepository.save(pageonce);
        return PageonceWriteResponseDto.builder()
                .success(true)
                .message("정상적으로 가계부가 작성되었습니다.")
                .expenditure(requestDto.getExpenditure())
                .memo(requestDto.getMemo())
                .build();

    }

    //가계부 상세조회
    public PageonceDetailsResponseDto detailView(UserDetailsImpl userDetails, Long pageonceId) {
        PageonceDetailsResponseDto pageonceDetailsResponseDto;
        Optional<Pageonce> pageonceOptional = pageonceRepository.findByIdAndDeleted(pageonceId, false);
        if (!pageonceOptional.isPresent()) {
            pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long ownerUserId = pageonce.getUserId();
            Long requesterId = userDetails.getUser().getId();
            if (!ownerUserId.equals(requesterId)) {
                pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                        .success(false)
                        .message("자신의 가계부만 조회할 수 있습니다")
                        .build();
            } else {
                pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                        .success(true)
                        .message("성공적으로 가계부를 불러왔습니다.")
                        .expenditure(pageonce.getExpenditure())
                        .memo(pageonce.getMemo())
                        .createdAt(pageonce.getCreatedAt())
                        .modifiedAt(pageonce.getModifiedAt())
                        .build();
            }
        }
        return pageonceDetailsResponseDto;
    }


    //가계부 수정
    @Transactional
    public PageonceWriteResponseDto modify(UserDetailsImpl userDetails,
                                           Long pageonceId,
                                           PageonceWriteRequestDto pageonceWriteRequestDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return PageonceWriteResponseDto.builder()
                    .success(false)
                    .message(message)
                    .build();
        }
        PageonceWriteResponseDto pageonceWriteResponseDto;
        Optional<Pageonce> pageonceOptional = pageonceRepository.findByIdAndDeleted(pageonceId, false);
        if (!pageonceOptional.isPresent()) {
            pageonceWriteResponseDto = PageonceWriteResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않거나 삭제된 가계부입니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long requesterId = userDetails.getUser().getId();
            Long ownerId = pageonce.getUserId();
            if (!requesterId.equals(ownerId)) {
                pageonceWriteResponseDto = PageonceWriteResponseDto.builder()
                        .success(false)
                        .message("자신의 가계부만 수정할 수 있습니다.")
                        .build();
            } else {
                pageonce.update(pageonceWriteRequestDto);
                pageonceWriteResponseDto = PageonceWriteResponseDto.builder()
                        .success(true)
                        .message("성공적으로 수정하였습니다.")
                        .expenditure(pageonceWriteRequestDto.getExpenditure())
                        .memo(pageonceWriteRequestDto.getMemo())
                        .build();
            }
        }
        return pageonceWriteResponseDto;
    }


    //가계부 삭제
    @Transactional
    public SimpleResponseDto delete(UserDetailsImpl userDetails, Long pageonceId) {
        Optional<Pageonce> pageonceOptional = pageonceRepository.findByIdAndDeleted(pageonceId, false);
        SimpleResponseDto simpleResponseDto;
        if (!pageonceOptional.isPresent()) {
            simpleResponseDto = SimpleResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않거나 이미 삭제된 가계부입니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long ownerId = pageonce.getUserId();
            Long requester = userDetails.getUser().getId();
            if (!ownerId.equals(requester)) {
                simpleResponseDto = SimpleResponseDto.builder()
                        .success(false)
                        .message("자신의 가계부만 삭제할 수 있습니다.")
                        .build();
            } else {
                pageonce.delete(true);
                simpleResponseDto = SimpleResponseDto.builder()
                        .success(true)
                        .message("성공적으로 삭제하였습니다.")
                        .build();
            }
        }
        return simpleResponseDto;
    }


    //가계부 복구
    @Transactional
    public SimpleResponseDto restore(UserDetailsImpl userDetails, Long pageonceId) {
        SimpleResponseDto simpleResponseDto;
        Optional<Pageonce> pageonceOptional = pageonceRepository.findByIdAndDeleted(pageonceId, true);
        if (!pageonceOptional.isPresent()) {
            simpleResponseDto = SimpleResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 삭제된 가계부가 존재하지 않습니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long ownerId = pageonce.getUserId();
            Long requester = userDetails.getUser().getId();
            if (!ownerId.equals(requester)) {
                simpleResponseDto = SimpleResponseDto.builder()
                        .success(false)
                        .message("자신의 가계부만 복구할 수 있습니다.")
                        .build();
            } else {
                pageonce.delete(false);
                simpleResponseDto = SimpleResponseDto.builder()
                        .success(true)
                        .message("성공적으로 복구하였습니다.")
                        .build();
            }
        }
        return simpleResponseDto;
    }

    //가계부 리스트 조회
    public PageonceViewResponseDto viewAll(UserDetailsImpl userDetails) {
        List<Pageonce> pageonceList = pageonceRepository.findByUserIdAndDeleted(userDetails.getUser().getId(), false);
        List<PageonceView> pageonceViewList = new ArrayList<>();
        for (Pageonce pageonce : pageonceList) {
            PageonceView pageonceView = PageonceView.builder()
                    .expenditure(pageonce.getExpenditure())
                    .memo(pageonce.getMemo())
                    .build();
            pageonceViewList.add(pageonceView);
        }
        return PageonceViewResponseDto.builder()
                .success(true)
                .message("성공적으로 가계부를 불러왔습니다.")
                .pageonceViewList(pageonceViewList)
                .build();
    }
}
