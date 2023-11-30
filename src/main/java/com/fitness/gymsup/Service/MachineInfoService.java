package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.MachineInfoDTO;
import com.fitness.gymsup.DTO.MachineUsageDTO;
import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Entity.MachineUsageEntity;
import com.fitness.gymsup.Entity.ReplyEntity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MachineInfoService {

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final MachineInfoRepository machineInfoRepository;
    private final MachineUsageRepository machineUsageRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final FileUploader fileUploader;

    public List<MachineInfoDTO> list() throws Exception {
        List<MachineInfoEntity> machineInfoEntities = machineInfoRepository.findAll();

        List<MachineInfoDTO> machineInfoDTOS = Arrays.asList(modelMapper.map(machineInfoEntities, MachineInfoDTO[].class));

        return machineInfoDTOS;
    }
    public MachineInfoDTO detail(Integer id) throws Exception {
        MachineInfoEntity machineInfoEntity = machineInfoRepository.findById(id).orElseThrow();

        MachineInfoDTO machineInfoDTO = modelMapper.map(machineInfoEntity, MachineInfoDTO.class);
        return machineInfoDTO;
    }
    public MachineInfoDTO find(String className) throws Exception {
        MachineInfoEntity machineInfoEntity = machineInfoRepository.findByResult(className);

        List<MachineUsageEntity> machineUsageEntities = machineUsageRepository
                .findAllByMachineInfoEntity(machineInfoEntity);
        List<MachineUsageDTO> machineUsageDTOS = Arrays.asList(modelMapper
                .map(machineUsageEntities, MachineUsageDTO[].class));

        MachineInfoDTO machineInfoDTO = modelMapper
                .map(machineInfoRepository.findByResult(className), MachineInfoDTO.class);
        machineInfoDTO.setMachineUsageDTOList(machineUsageDTOS);

        return machineInfoDTO;
    }

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

    public void modify(MachineInfoDTO machineInfoDTO, MultipartFile imgFile) throws Exception {
        //기존파일 삭제
        MachineInfoEntity machineInfoEntity = machineInfoRepository.findById(machineInfoDTO.getId()).orElseThrow();
        String deleteFile = machineInfoEntity.getImg();


        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";
        if(originalFileName.length() != 0) {
            if(deleteFile.length() != 0) {
                fileUploader.deleteFile(imgUploadLocation, deleteFile);
            }

            newFileName = fileUploader.uploadFile(
                    imgUploadLocation,
                    originalFileName,
                    imgFile.getBytes());

            machineInfoDTO.setImg((newFileName));

        }
        machineInfoDTO.setId(machineInfoEntity.getId());

        //변환
        MachineInfoEntity data = modelMapper.map(machineInfoDTO, MachineInfoEntity.class);
        machineInfoRepository.save(data);
    }

    public void remove() throws Exception {}

}
