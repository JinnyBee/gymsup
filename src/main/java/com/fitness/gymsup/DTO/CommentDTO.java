package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
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
public class CommentDTO {
    private Integer id;                         //댓글 id

    private Integer userId;                     //댓글 작성자 id (of user 테이블)
    private String userNickname;                //댓글 작성자 닉네임 (of user 테이블)

    private Integer boardId;                    //부모 게시글 id
    private String boardTitle;                  //부모 게시글 제목
    private BoardCategoryType boardCategoryType;//부모 게시글 카테고리 타입 (열거형)
    private Integer boardViewCnt;               //부모 게시글 조회수
    private LocalDateTime boardRegDate;         //부모 게시글

    @NotEmpty(message = "댓글을 적어주세요.")
    private String  comment;                    //댓글 내용

    private Integer goodCnt;                    //추천수
    private List<ReplyDTO> replyDTOList;        //답글 리스트
    
    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}