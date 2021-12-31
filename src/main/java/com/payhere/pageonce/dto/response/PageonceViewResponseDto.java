package com.payhere.pageonce.dto.response;

import com.payhere.pageonce.model.PageonceView;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PageonceViewResponseDto {
    private boolean success;
    private String message;
    private List<PageonceView> pageonceViewList;

    @Builder
    public PageonceViewResponseDto(boolean success,String message,List<PageonceView> pageonceViewList){
        this.message = message;
        this.success = success;
        this.pageonceViewList = pageonceViewList;
    }
}
