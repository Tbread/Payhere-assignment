package com.payhere.pageonce.model;

import com.payhere.pageonce.dto.request.PageonceWriteRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Pageonce extends TimeStamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String memo;

    @Column(nullable = false)
    private Long expenditure;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public Pageonce(String memo,Long expenditure,Long userId){
        this.memo = memo;
        this.expenditure = expenditure;
        this.deleted = false;
        this.userId = userId;
    }

    public void update(PageonceWriteRequestDto pageonceWriteRequestDto){
        this.memo = pageonceWriteRequestDto.getMemo();
        this.expenditure = pageonceWriteRequestDto.getExpenditure();
    }

}
