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
        Optional<Pageonce> pageonce = pageonceRepository.findById(pageonceId);
        if(!pageonce.isPresent()){
            pageonceDetailsResponseDto = PageonceDetailsResponseDto.builder()
                    .success(false)
                    .message("해당 ID의 가계부가 존재하지 않습니다.")
                    .build();
        } else {
            Pageonce pageonce1 = pageonce.get();
            Long ownerUserId = pageonce1.getUserId();
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
                        .expenditure(pageonce1.getExpenditure())
                        .memo(pageonce1.getMemo())
                        .createdAt(pageonce1.getCreatedAt())
                        .modifiedAt(pageonce1.getModifiedAt())
                        .build();
            }
        }
        return pageonceDetailsResponseDto;
    }

    
}
