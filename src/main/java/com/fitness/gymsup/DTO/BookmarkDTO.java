package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.Entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkDTO {
    private Integer id;                     //북마크 id
    private Integer userId;                 //북마크를 누른 사용자 id
    private Integer boardId;                //북마크 게시글의 id
    private String boardTitle;              //북마크 게시글의 제목
    private Integer boardViewCnt;           //북마크 게시글의 조회수
    private LocalDateTime boardRegDate;
    private BoardCategoryType categoryType; //북마크 게시글의 카테고리 타입 (열거형)
    private BookmarkType bookmarkType;      //북마크 타입 (열거형)

    private LocalDateTime regDate;          //생성일
    private LocalDateTime modDate;          //수정일
}
