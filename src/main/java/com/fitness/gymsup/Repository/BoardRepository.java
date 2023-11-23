package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Entity.BoardEntity;
import com.fitness.gymsup.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BoardRepository extends
        JpaRepository<BoardEntity, Integer> {
    @Query(value = "UPDATE board SET view_cnt = view_cnt+1 WHERE id=:id",
            nativeQuery = true)
    void viewcnt(@Param("id") Integer id);

    Page<BoardEntity> findAllByCategoryType(Pageable pageable, BoardCategoryType categoryType);
}