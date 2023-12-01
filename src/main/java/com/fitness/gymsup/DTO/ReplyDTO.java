package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {
    private Integer id;                 //답글 id
    private Integer commentId;          //원 댓글 id
    private Integer boardId;            //원 게시글 id
    private Integer userId;             //답글 작성자 id
    private String userNickname;        //답글 작성자 닉네임
    private BoardCategoryType boardCategoryType;

    @NotEmpty(message = "답글을 적어주세요.")
    private String  reply;              //답글 내용

    private LocalDateTime regDate;      //생성일
    private LocalDateTime modDate;      //수정일
}