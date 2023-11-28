package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MachineUsageDTO;
import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Entity.MachineUsageEntity;
import com.fitness.gymsup.Repository.MachineInfoRepository;
import com.fitness.gymsup.Repository.MachineUsageRepository;
import com.fitness.gymsup.Util.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j

public class MachineUsageService {

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final MachineUsageRepository machineUsageRepository;
    private final MachineInfoRepository machineInfoRepository;
    private final FileUploader fileUploader;
    private ModelMapper modelMapper = new ModelMapper();

    public void register(MachineUsageDTO machineUsageDTO, MultipartFile imgFile)throws Exception{
        MachineUsageEntity machineUsageEntity = modelMapper.map(machineUsageDTO, MachineUsageEntity.class);
        String originalFileName = "";
        String newFileName = "";

        Optional<MachineInfoEntity> machineInfo = machineInfoRepository.findById(machineUsageDTO.getId());

    }
}
