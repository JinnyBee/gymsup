package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.ContactEntity;
import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {

    //유저정보를 이용하여 모든 문의 조회
    List<ContactEntity> findAllByUserEntity(UserEntity userEntity);

    void deleteAllByUserEntity(UserEntity userEntity);
}
