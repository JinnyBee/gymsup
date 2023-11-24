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
    private Integer id;                         //게시판 id
    private BoardCategoryType categoryType;     //게시판 카테고리 타입 (열거형)
    private Integer userId;                     //게시글 작성자 id
    private String  userNickname;               //게시글 작성자 닉네임

    @NotEmpty(message = "제목을 적어주세요.")
    private String  title;                      //게시글 제목
    @NotEmpty(message = "내용을 적어주세요.")
    private String  content;                    //게시글 내용

    private List<String> imgFileList;           //게시글 첨부 이미지파일 리스트
    private Integer goodCnt;                    //게시글 추천수
    private Integer viewCnt;                    //게시글 조회수
    
    private Boolean bookmarkOn;                 //게시글 조회하고 있는 사용자가 북마크 눌렀는지
    private Boolean GoodOn;                     //게시글 조회하고 있는 사용자가 좋아요 눌렀는지
    
    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}