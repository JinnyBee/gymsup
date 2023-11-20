package com.fitness.gymsup.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Integer id;             //유저 id
    private String email;           //유저 이메일
    private String nickname;        //유저 닉네임
    private String password;        //유저 비밀번호
    private LocalDateTime regDate;  //생성일
    private LocalDateTime modDate;  //수정일
}
