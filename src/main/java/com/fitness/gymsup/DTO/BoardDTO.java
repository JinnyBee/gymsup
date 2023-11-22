package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private Integer id;                 //게시판 id
    private BoardCategoryType categoryType;   //게시판 카테고리 분류
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