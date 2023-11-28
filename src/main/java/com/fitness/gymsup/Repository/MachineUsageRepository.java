package com.fitness.gymsup.Repository;

import com.fitness.gymsup.Entity.MachineUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineUsageRepository extends JpaRepository<MachineUsageEntity, Integer> {
}
