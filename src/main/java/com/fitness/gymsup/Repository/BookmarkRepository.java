package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.BookmarkEntity;
import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Integer> {

    BookmarkEntity findAllByBoardEntityAndUserEntity(BoardEntity boardEntity, UserEntity userEntity);
    Page<BookmarkEntity> findAllByUserEntity(Pageable pageable, UserEntity userEntity);
    Page<BookmarkEntity> findAllByUserEntityAndBookmarkType(Pageable pageable, UserEntity userEntity, BookmarkType bookmarkType);
    Long countAllByUserEntityAndBoardEntityAndBookmarkType(UserEntity userEntity, BoardEntity boardEntity, BookmarkType bookmarkType);

    @Query(value = "DELETE FROM bookmark WHERE user_id = :userId and board_id = :boardId",
            nativeQuery = true)
    void deleteAllByUserIdAndBoardId(@Param("userId") Integer userId, @Param("boardId") Integer boardId);
}