package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.BoardImageEntity;
import com.fitness.gymsup.Entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BoardImageRepository extends
        JpaRepository<BoardImageEntity, Integer> {
    @Query(value = "SELECT * FROM board_image WHERE board_id = :boardId",
           nativeQuery = true)
    List<BoardImageEntity> findAllByBoardEntity(Integer boardId);

    @Query(value = "DELETE FROM board_image WHERE board_id = :boardId",
           nativeQuery = true)
    void deleteAllByBoardEntity(Integer boardId);
}