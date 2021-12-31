package com.payhere.pageonce.service;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.dto.response.PageonceDetailsResponseDto;
import com.payhere.pageonce.dto.response.PageonceWriteResponseDto;
import com.payhere.pageonce.model.Pageonce;
import com.payhere.pageonce.repository.PageonceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageonceService {
    private final PageonceRepository pageonceRepository;

    @Transactional
    public PageonceWriteResponseDto write(UserDetailsImpl userDetails, PageonceWriteRequestDto requestDto) {
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

    public PageonceDetailsResponseDto detailView(UserDetailsImpl userDetails, Long pageonceId){
        PageonceDetailsResponseDto pageonceDetailsResponseDto;
        Optional<Pageonce> pageonceOptional = pageonceRepository.findById(pageonceId);
        if(!pageonceOptional.isPresent()){
            pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않습니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long ownerUserId = pageonce.getUserId();
            Long requesterId = userDetails.getUser().getId();
            if(!ownerUserId.equals(requesterId)){
                pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                        .success(false)
                        .message("자신의 가계부만 조회할 수 있습니다")
                        .build();
            } else {
                pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                        .success(true)
                        .message("성공적으로 불러왔습니다.")
                        .expenditure(pageonce.getExpenditure())
                        .memo(pageonce.getMemo())
                        .createdAt(pageonce.getCreatedAt())
                        .modifiedAt(pageonce.getModifiedAt())
                        .build();
            }
        }
        return pageonceDetailsResponseDto;
    }

    @Transactional
    public PageonceWriteResponseDto modify(UserDetailsImpl userDetails,Long pageonceId,PageonceWriteRequestDto pageonceWriteRequestDto){
        PageonceWriteResponseDto pageonceWriteResponseDto;
        Optional<Pageonce> pageonceOptional = pageonceRepository.findById(pageonceId);
        if(!pageonceOptional.isPresent()){
            pageonceWriteResponseDto = PageonceWriteResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않습니다.")
                    .build();
        } else {
            Pageonce pageonce = pageonceOptional.get();
            Long requesterId = userDetails.getUser().getId();
            Long ownerId = pageonce.getUserId();
            if(!requesterId.equals(ownerId)){
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

}
