package com.payhere.pageonce.controller;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.dto.response.PageonceDetailsResponseDto;
import com.payhere.pageonce.dto.response.PageonceWriteResponseDto;
import com.payhere.pageonce.service.PageonceService;
import com.payhere.pageonce.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PageonceController {
    private final PageonceService pageonceService;

    @PostMapping("/write")
    public PageonceWriteResponseDto write(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PageonceWriteRequestDto pageonceWriteRequestDto){
        return pageonceService.write(userDetails,pageonceWriteRequestDto);
    }

    @GetMapping("/view/{pageonceId}")
    public PageonceDetailsResponseDto detailview(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long pageonceId){
        return pageonceService.detailView(userDetails,pageonceId);
    }
}
