package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Entity.MachineUsageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineUsageRepository extends JpaRepository<MachineUsageEntity, Integer> {

    //운동기구로 전체를 검색하고 난 뒤 페이지처리
    Page<MachineUsageEntity> findAllByMachineInfoEntity(Pageable pageable, MachineInfoEntity machineInfoEntity);

    //운동기구로 전체를 검색하고 난 뒤 리스트처리
    List<MachineUsageEntity> findAllByMachineInfoEntity(MachineInfoEntity machineInfoEntity);

    //조회수
    @Query(value = "UPDATE machine_usage set view_cnt = view_cnt+1 where id=:id", nativeQuery = true)
    void viewCntUp(@Param("id") Integer id);
}
