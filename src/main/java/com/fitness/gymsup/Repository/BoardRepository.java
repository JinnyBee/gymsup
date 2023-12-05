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
    //검색이 없을 경우 페이지처리 :  findAll 사용
    //제목으로 검색시 페이지 처리
    @Query("SELECT b FROM BoardEntity b WHERE b.categoryType=:categoryType and b.title like %:keyword%")
    Page<BoardEntity> searchTitle(Pageable pageable, BoardCategoryType categoryType, String keyword);

    //내용으로 검색시 페이지 처리
    @Query("SELECT b FROM BoardEntity b WHERE b.categoryType=:categoryType and b.content like %:keyword%")
    Page<BoardEntity> searchContent(Pageable pageable, BoardCategoryType categoryType, String keyword);

    //닉네임으로 검색시 페이지처리
    @Query("SELECT b FROM BoardEntity b LEFT JOIN b.userEntity u WHERE b.categoryType=:categoryType and u.nickname like %:keyword%")
    Page<BoardEntity> searchNickname(Pageable pageable, BoardCategoryType categoryType, String keyword);

    @Query("SELECT b FROM BoardEntity b LEFT JOIN b.userEntity u WHERE b.categoryType=:categoryType")
    Page<BoardEntity> searchNickname2(Pageable pageable, BoardCategoryType categoryType);

    //제목+내용으로 검색시 페이지처리
    @Query("SELECT b FROM BoardEntity b WHERE b.categoryType=:categoryType and b.title like %:keyword% or b.content like %:keyword%")
    Page<BoardEntity> searchTitleContent(Pageable pageable, BoardCategoryType categoryType, String keyword);

    //제목+내용+닉네임으로 검색시 페이지처리
    @Query("SELECT b FROM BoardEntity b LEFT JOIN b.userEntity u WHERE b.categoryType=:categoryType and b.title like %:keyword% or b.content like %:keyword% or u.nickname like %:keyword%")
    Page<BoardEntity> searchTitleContentNickname(Pageable pageable, BoardCategoryType categoryType, String keyword);

    //특정게시판 제외한 게시판 전체 목록
    @Query("SELECT b FROM BoardEntity b WHERE b.categoryType<>:categoryType")
    Page<BoardEntity> findAllWithoutCategoryType(Pageable pageable, BoardCategoryType categoryType);

    @Query(value = "UPDATE board SET view_cnt = view_cnt+1 WHERE id=:id",
            nativeQuery = true)
    void viewCntUp(@Param("id") Integer id);

    @Query(value = "UPDATE board SET good_cnt = good_cnt+1 WHERE id=:id",
            nativeQuery = true)
    void goodCntUp(@Param("id") Integer id);

    Page<BoardEntity> findAllByCategoryType(Pageable pageable, BoardCategoryType categoryType);
    Page<BoardEntity> findAllByUserEntity(Pageable pageable, UserEntity userEntity);
    List<BoardEntity> findTop2ByCategoryTypeOrderByGoodCntDesc(BoardCategoryType categoryType);
    List<BoardEntity> findTop5ByCategoryTypeOrderByRegDateDesc(BoardCategoryType categoryType);
}