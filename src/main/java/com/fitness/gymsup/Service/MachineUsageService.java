/*
    파일명 : MachineUsageService.java
    기 능 : 운동기구 영상 전체목록/부분목록, 운동기구 영상 등록/상세보기/수정/삭제
    작성일 : 2023.12.08
    작성자 : 이민호
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MachineUsageDTO;
import com.fitness.gymsup.Entity.MachineInfoEntity;
import com.fitness.gymsup.Entity.MachineUsageEntity;
import com.fitness.gymsup.Repository.MachineInfoRepository;
import com.fitness.gymsup.Repository.MachineUsageRepository;
import com.fitness.gymsup.Util.FileUploader;
import com.fitness.gymsup.Util.S3Uploader;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MachineUsageService {

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final S3Uploader s3Uploader;
    private final MachineUsageRepository machineUsageRepository;
    private final MachineInfoRepository machineInfoRepository;
    private final FileUploader fileUploader;
    private ModelMapper modelMapper = new ModelMapper();

    //운동기구 영상 전체목록
    public Page<MachineUsageDTO> listAll(Pageable page) throws Exception {

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

    //운동기구 영상 부분 목록
    public Page<MachineUsageDTO> partList(int id,
                                          Pageable page) throws Exception {

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

    //운동기구 영상 등록
    public void register(MachineUsageDTO machineUsageDTO,
                         MultipartFile imgFile) throws Exception {

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName !=null) {
            newFileName = s3Uploader.upload(imgFile,imgUploadLocation);
        }
        machineUsageDTO.setThumbnail(newFileName);
        MachineInfoEntity machineInfo = machineInfoRepository.findById(machineUsageDTO.getMachineInfoId()).orElseThrow();

        MachineUsageEntity machineUsageEntity = modelMapper.map(machineUsageDTO, MachineUsageEntity.class);
        machineUsageEntity.setMachineInfoEntity(machineInfo);

        machineUsageRepository.save(machineUsageEntity);
    }

    //운동기구 영상 상세보기
    public MachineUsageDTO detail(int id,
                                  boolean isFirst) throws Exception {

        if(isFirst) {
            machineUsageRepository.viewCntUp(id);
        }
        MachineUsageEntity machineUsageEntity = machineUsageRepository.findById(id).orElseThrow();
        MachineUsageDTO machineUsageDTO = modelMapper.map(machineUsageEntity, MachineUsageDTO.class);

        return machineUsageDTO;
    }

    //운동기구 영상 수정
    public void modify(MachineUsageDTO machineUsageDTO,
                       MultipartFile imgFile) throws Exception {

        MachineUsageEntity machineUsageEntity = machineUsageRepository.findById(machineUsageDTO.getId()).orElseThrow();
        MachineInfoEntity machineInfoEntity = machineInfoRepository.findById(machineUsageDTO.getMachineInfoId()).orElseThrow();

        String deleteFile = machineUsageEntity.getThumbnail();
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName= "";

        if (originalFileName.length()!= 0) {
            if(deleteFile.length() != 0) {
                s3Uploader.deleteFile(deleteFile, imgUploadLocation);
            }

            newFileName = s3Uploader.upload(imgFile, imgUploadLocation);
            machineUsageDTO.setThumbnail(newFileName);
        }
        machineUsageDTO.setId(machineUsageEntity.getId());
        MachineUsageEntity data = modelMapper.map(machineUsageDTO, MachineUsageEntity.class);
        data.setMachineInfoEntity(machineInfoEntity);

        machineUsageRepository.save(data);
    }

    //운동기구 영상 삭제
    public void delete(Integer id) throws Exception {

        MachineUsageEntity machineUsageEntity = machineUsageRepository.findById(id).orElseThrow();
        s3Uploader.deleteFile(machineUsageEntity.getThumbnail(), imgUploadLocation);

        machineUsageRepository.deleteById(id);
    }

}
