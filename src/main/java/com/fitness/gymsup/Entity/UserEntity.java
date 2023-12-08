package com.fitness.gymsup.Entity;

import com.fitness.gymsup.Constant.UserRole;
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
public class UserEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "user_SEQ")
    private Integer id;             //유저 id

    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;           //유저 이메일

    @Column(name = "nickname", nullable = false, length = 200)
    private String nickname;        //유저 닉네임

    @Column(name = "password")
    private String password;        //유저 비밀번호

    @Column(name = "oauth_type", columnDefinition = "VARCHAR(50)")
    private String oauthType;       //간편로그인 타입 (구글, 네이버, 카카오)

    @Enumerated(EnumType.STRING)
    private UserRole role;          //유저 역할

    private boolean ban;            //유저 정지 여부
}