package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends
        JpaRepository<CommentEntity, Integer> {
    @Query(value = "SELECT * FROM comment WHERE board_id = :boardId ORDER BY mod_date DESC",
           nativeQuery = true)
    List<CommentEntity> findByBoardId(Integer boardId);

    @Query(value = "DELETE FROM comment WHERE board_id = :boardId",
           nativeQuery = true)
    void deleteAllByBoardId(Integer boardId);
}