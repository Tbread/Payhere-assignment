package com.payhere.pageonce.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageonceView {
    private String memo;
    private Long expenditure;

    @Builder
    public PageonceView(String memo,Long expenditure){
        this.expenditure = expenditure;
        this.memo = memo;
    }
}
