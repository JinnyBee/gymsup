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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName !=null){
            newFileName = fileUploader.uploadFile(imgUploadLocation, originalFileName,imgFile.getBytes());
        }
        machineUsageDTO.setThumbnail(newFileName);
        MachineInfoEntity machineInfo = machineInfoRepository.findById(machineUsageDTO.getMachineInfoId()).orElseThrow();

        MachineUsageEntity machineUsageEntity = modelMapper.map(machineUsageDTO, MachineUsageEntity.class);
        machineUsageEntity.setMachineInfoEntity(machineInfo);

        machineUsageRepository.save(machineUsageEntity);

    }

    //전체목록
    public Page<MachineUsageDTO> listAll(Pageable page)throws Exception{
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        Page<MachineUsageEntity> machineUsageEntities = machineUsageRepository.findAll(pageable);
        Page<MachineUsageDTO> machineUsageDTOS = machineUsageEntities.map(data->MachineUsageDTO.builder()
                .id(data.getId())
                .title(data.getTitle())
                .content(data.getContent())
                .machineInfoId(data.getMachineInfoEntity().getId())
                .machineInfoName(data.getMachineInfoEntity().getName())
                .url(data.getUrl())
                .thumbnail(data.getThumbnail())
                .viewCnt(data.getViewCnt())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build());

        return machineUsageDTOS;
    }

    //부분목록
    public Page<MachineUsageDTO> partList(int id, Pageable page)throws Exception{
        int curPage = page.getPageNumber()-1;
        int pageLimit = 5;

        Pageable pageable = PageRequest.of(curPage, pageLimit,
                Sort.by(Sort.Direction.DESC, "id"));

        MachineInfoEntity machineInfo = machineInfoRepository.findById(id).orElseThrow();
        Page<MachineUsageEntity> machineUsageEntities = machineUsageRepository.findAllByMachineInfoEntity(pageable, machineInfo);
        Page<MachineUsageDTO> machineUsageDTOS = machineUsageEntities.map(data->MachineUsageDTO.builder()
                .id(data.getId())
                .title(data.getTitle())
                .content(data.getContent())
                .machineInfoId(data.getMachineInfoEntity().getId())
                .machineInfoName(data.getMachineInfoEntity().getName())
                .url(data.getUrl())
                .thumbnail(data.getThumbnail())
                .viewCnt(data.getViewCnt())
                .regDate(data.getRegDate())
                .modDate(data.getModDate())
                .build());

        return machineUsageDTOS;
    }

}
