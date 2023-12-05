package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<UserEntity, Integer> {
    //이메일로 유저정보 조회
    UserEntity findByEmail(String email);

    //닉네임 중복체크 있으면 1 없으면 0
    Long countByNickname(String nickname);

    //이메일 중복체크 있으면 1 없으면 0
    Long countByEmail(String email);

    //간편로그인타입, 이메일로 조회
    Optional<UserEntity> findByEmailAndOauthType(String email, String oauthType);

    //수정 필요.
    void deleteByEmail(String email);
}