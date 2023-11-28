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

import java.util.List;


@Repository
public interface BoardRepository extends
        JpaRepository<BoardEntity, Integer> {
    //검색이 없는 페이지처리 FindAll 사용
    //제목으로 검색시 페이지 처리
    @Query("SELECT u FROM BoardEntity u where u.title like %:keyword%")
    Page<BoardEntity> searchTitle(String keyword, Pageable pageable);

    //내용으로 검색시 페이지 처리
    @Query("SELECT u FROM BoardEntity u where u.content like %:keyword%")
    Page<BoardEntity> searchContent(String keyword, Pageable pageable);

    //작성자로 검색시 페이지처리
    @Query("SELECT u FROM BoardEntity u where u.userEntity.email like %:keyword%")
    Page<BoardEntity> searchWriter(String keyword, Pageable pageable);

    @Query(value = "UPDATE board SET view_cnt = view_cnt+1 WHERE id=:id",
            nativeQuery = true)
    void viewCntUp(@Param("id") Integer id);

    @Query(value = "UPDATE board SET good_cnt = good_cnt+1 WHERE id=:id",
            nativeQuery = true)
    void goodCntUp(@Param("id") Integer id);

    Page<BoardEntity> findAllByCategoryType(Pageable pageable, BoardCategoryType categoryType);
    Page<BoardEntity> findAllByUserEntity(Pageable pageable, UserEntity userEntity);
    List<BoardEntity> findTop2ByCategoryTypeOrderByGoodCntDesc(BoardCategoryType categoryType);
}