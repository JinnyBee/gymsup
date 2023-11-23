package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BookmarkEntity;
import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    BookmarkEntity findAllByBoardEntityAndUserEntity(BoardEntity boardEntity, UserEntity userEntity);
    Page<BookmarkEntity> findAllByUserEntity(Pageable pageable, UserEntity userEntity);
}
