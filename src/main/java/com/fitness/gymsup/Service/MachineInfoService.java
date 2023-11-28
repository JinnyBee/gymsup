package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Repository.MachineInfoRepository;
import com.fitness.gymsup.Util.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MachineInfoService {

    private final MachineInfoRepository machineInfoRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final FileUploader fileUploader;

    public void register(MachineInfoDTO machineInfoDTO){
        MachineInfoEntity machineInfoEntity = modelMapper.map(machineInfoDTO, MachineInfoEntity.class);
        String originalFileName = "";
        String newFileName = "";

        machineInfoRepository.save(machineInfoEntity);
    }
}
