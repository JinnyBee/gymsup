package com.fitness.gymsup.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SequenceGenerator(
        name = "user_SEQ",
        sequenceName = "user_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "user_SEQ")
    private Integer id;             //유저 id

    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;           //유저 이메일

    @Column(name = "nickname", nullable = false, length = 200)
    private String nickname;        //유저 닉네임

    @Column(name = "password", nullable = false)
    private String password;        //유저 비밀번호
}