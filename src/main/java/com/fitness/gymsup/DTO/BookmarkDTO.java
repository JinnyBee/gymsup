package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
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
    private Integer id;
    private Integer boardId;
    private String boardTitle;
    private BoardCategoryType categoryType;
    private Integer boardViewCnt;
    private Integer userId;
    private boolean isBookmark;

    private LocalDateTime regDate;      //생성일
    private LocalDateTime modDate;      //수정일
}
