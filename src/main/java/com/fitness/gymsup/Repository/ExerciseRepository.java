package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.ExerciseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Integer> {
    @Query("select b from ExerciseEntity b where b.exercisename like %:keyword%")
    Page<ExerciseEntity> searchExercisename(Pageable pageable, String keyword);
}
