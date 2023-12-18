package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
    private Integer id;                         //게시판 id
    private BoardCategoryType categoryType;     //게시판 카테고리 타입 (열거형)
    private Integer userId;                     //게시글 작성자 id (of user 테이블)
    private String  userNickname;               //게시글 작성자 닉네임 (of user 테이블)

    @NotEmpty(message = "제목을 적어주세요.")
    private String  title;                      //게시글 제목

    @NotEmpty(message = "내용을 적어주세요.")
    //@Size(min = 1, max = 5000)
    @Size(min = 1, max = 100000000)
    private String  content;                    //게시글 내용

    private List<String> imgFileList;           //게시글 첨부 이미지파일 리스트
    private Integer viewCnt;                    //게시글 조회수
    private Integer goodCnt;                    //게시글 추천수
    private Integer commentCount;               //게시글 댓글 수

    private Integer loginUserId;                //게시글 조회하고 있는 사용자 id
    private Boolean bookmarkOn;                 //게시글 조회하고 있는 사용자가 북마크 눌렀는지
    private Boolean goodOn;                     //게시글 조회하고 있는 사용자가 좋아요 눌렀는지
    
    private String displayRegDate;              //생성일 표시
    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}