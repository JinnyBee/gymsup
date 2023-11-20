package com.fitness.gymsup.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCategoryDTO {
    private Integer id;             //게시판 카테고리 id
    private String category;        //게시판 카테고리명
    private LocalDateTime regDate;  //생성일
    private LocalDateTime modDate;  //수정일
}