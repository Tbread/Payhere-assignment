package com.payhere.pageonce.controller;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import com.payhere.pageonce.dto.response.PageonceViewResponseDto;
import com.payhere.pageonce.dto.response.SimpleResponseDto;
import com.payhere.pageonce.dto.response.PageonceDetailsResponseDto;
import com.payhere.pageonce.dto.response.PageonceWriteResponseDto;
import com.payhere.pageonce.service.PageonceService;
import com.payhere.pageonce.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PageonceController {
    private final PageonceService pageonceService;

    @PostMapping("/write")
    public PageonceWriteResponseDto write(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody @Valid PageonceWriteRequestDto pageonceWriteRequestDto,
                                          BindingResult bindingResult) {
        return pageonceService.write(userDetails, pageonceWriteRequestDto,bindingResult);
    }

    @GetMapping("/view/{pageonceId}")
    public PageonceDetailsResponseDto detailview(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long pageonceId) {
        return pageonceService.detailView(userDetails, pageonceId);
    }

    @PatchMapping("/modify/{pageonceId}")
    public PageonceWriteResponseDto modify(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable Long pageonceId,
                                           @RequestBody @Valid PageonceWriteRequestDto pageonceWriteRequestDto,
                                           BindingResult bindingResult) {
        return pageonceService.modify(userDetails, pageonceId, pageonceWriteRequestDto,bindingResult);
    }

    @PatchMapping("/delete/{pageonceId}")
    public SimpleResponseDto delete(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long pageonceId) {
        return pageonceService.delete(userDetails, pageonceId);
    }

    @PatchMapping("/restore/{pageonceId}")
    public SimpleResponseDto restore(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long pageonceId) {
        return pageonceService.restore(userDetails, pageonceId);
    }

    @GetMapping("/view")
    public PageonceViewResponseDto viewAll(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return pageonceService.viewAll(userDetails);
    }
}
