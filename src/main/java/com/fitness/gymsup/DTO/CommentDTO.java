package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.ReplyEntity;
import com.fitness.gymsup.Entity.UserEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private Integer id;                     //댓글 id
    private Integer boardId;                //원 게시글 id
    private Integer userId;                 //user 테이블의 댓글 작성자 id
    private String userNickname;            //user 테이블의 댓글 작성자 닉네임

    @NotEmpty(message = "댓글을 적어주세요.")
    private String  comment;                //댓글 내용

    private Integer goodCnt;                //추천수
    private List<ReplyDTO> replyDTOList;    //답글 리스트
    private LocalDateTime regDate;          //생성일
    private LocalDateTime modDate;          //수정일
}