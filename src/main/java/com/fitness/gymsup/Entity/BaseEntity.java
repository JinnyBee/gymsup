package com.fitness.gymsup.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Column(name="reg_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime regDate; //생성날짜

    @Column(name="mod_date")
    @LastModifiedDate
    private LocalDateTime modDate; //수정날짜
}
