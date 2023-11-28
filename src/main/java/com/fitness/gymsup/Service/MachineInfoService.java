package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Repository.MachineInfoRepository;
import com.fitness.gymsup.Util.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MachineInfoService {

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final MachineInfoRepository machineInfoRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final FileUploader fileUploader;

    public void register(MachineInfoDTO machineInfoDTO, MultipartFile imgFile)throws Exception{
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if (originalFileName !=null){
            newFileName = fileUploader.uploadFile(imgUploadLocation, originalFileName, imgFile.getBytes());
        }
        machineInfoDTO.setImg(newFileName);
        MachineInfoEntity machineInfoEntity = modelMapper.map(machineInfoDTO, MachineInfoEntity.class);
        machineInfoRepository.save(machineInfoEntity);
    }
}
