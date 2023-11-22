package com.fitness.gymsup.DTO;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Integer id;             //유저 id

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;           //유저 이메일

    @NotEmpty(message = "닉네임을 입력해 주세요.")
    private String nickname;        //유저 닉네임

    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    @Length(min=8, max=16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
    private String password;        //유저 비밀번호
    private LocalDateTime regDate;  //생성일
    private LocalDateTime modDate;  //수정일
}
