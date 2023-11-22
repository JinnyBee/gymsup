package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
    Long countByNickname(String nickname);
    Long countByEmail(String email);

    Optional<UserEntity> findByEmailAndOauthType(String email, String oauthType);
}