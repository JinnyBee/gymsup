package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.BoardCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCategoryRepository extends
        JpaRepository<BoardCategoryEntity, Integer> {
}