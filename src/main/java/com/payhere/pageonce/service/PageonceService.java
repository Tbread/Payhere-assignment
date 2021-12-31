package com.payhere.pageonce.service;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.dto.response.PageonceWriteResponseDto;
import com.payhere.pageonce.model.Pageonce;
import com.payhere.pageonce.repository.PageonceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
}
