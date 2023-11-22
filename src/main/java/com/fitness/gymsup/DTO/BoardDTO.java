package com.fitness.gymsup.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private Integer id;                 //게시판 id
    private Integer categoryId;         //게시판 카테고리 id
    private Integer userId;             //유저테이블 id
    private String  userNickname;       //유저테이블 닉네임
    @NotEmpty(message = "제목을 적어주세요.")
    private String  title;              //제목
    @NotEmpty(message = "내용을 적어주세요.")
    private String  content;            //내용
    private List<String> imgFileList;   //이미지파일 리스트
    private Integer goodCnt;            //추천수
    private Integer viewCnt;            //조회수
    private LocalDateTime regDate;      //생성일
    private LocalDateTime modDate;      //수정일
}