package com.fitness.gymsup.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {
    private Integer id;
    private Integer userId;
    private String userNickname;

    @NotEmpty(message = "제목을 적어주세요.")
    private String title;

    @NotEmpty(message = "내용을 적어주세요.")
    private String content;

    private String answer;

    private boolean is_answer;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
