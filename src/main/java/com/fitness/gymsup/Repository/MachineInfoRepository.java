package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.MachineInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineInfoRepository extends JpaRepository<MachineInfoEntity, Integer> {
    MachineInfoEntity findByResult(String result);

}