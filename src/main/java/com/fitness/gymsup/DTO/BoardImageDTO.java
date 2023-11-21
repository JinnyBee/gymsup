package com.fitness.gymsup.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardImageDTO {
    private Integer id;             //이미지파일 id
    private Integer boardId;        //게시판테이블 id
    private String  imgFile;        //이미지 파일
    private LocalDateTime regDate;  //생성일
    private LocalDateTime modDate;  //수정일
}