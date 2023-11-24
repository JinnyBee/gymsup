package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    @Query(value = "SELECT * FROM comment WHERE board_id = :boardId ORDER BY mod_date DESC",
            nativeQuery = true)
    List<CommentEntity> findByBoardId(@Param("boardId") Integer boardId);

    @Query(value = "DELETE FROM comment WHERE board_id = :boardId",
            nativeQuery = true)
    void deleteAllByBoardId(@Param("boardId") Integer boardId);
}