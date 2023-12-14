package com.fitness.gymsup.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {
    private Integer id;                             //문의 번호
    private Integer userId;                         //유저 번호
    private String userNickname;                    //유저 닉네임
    private String userEmail;                       //유저 이메일

    @NotEmpty(message = "제목을 적어주세요.")
    private String title;                           //문의 제목

    @NotEmpty(message = "내용을 적어주세요.")
    @Size(min = 1, max = 5000)
    private String content;                         //문의 내용

    private String answer;                          //답변

    private boolean is_answer;                      //답변여부

    private LocalDateTime regDate;                  //생성일
    private LocalDateTime modDate;                  //수정일
}
