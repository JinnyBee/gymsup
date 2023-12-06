package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.ReplyEntity;
import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
    @Query(value = "SELECT * FROM reply WHERE comment_id = :commentId ORDER BY mod_date ASC",
            nativeQuery = true)
    List<ReplyEntity> findByCommentId(Integer commentId);

    @Query(value = "DELETE FROM reply WHERE comment_id = :commentId",
            nativeQuery = true)
    void deleteAllByCommentId(Integer commentId);

    void deleteAllByUserEntity(UserEntity userEntity);
}