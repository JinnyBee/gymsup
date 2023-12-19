/*
    파일명 : MachineInfoService.java
    기 능 : 운동기구 정보 전체목록, 운동기구 정보 상세보기, 운동기구 정보 찾기, 운동기구 정보 등록/수정
    작성일 : 2023.12.08
    작성자 : 이민호, 전현진
*/
package com.fitness.gymsup.Service;

import com.fitness.gymsup.DTO.MachineInfoDTO;
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
    //S3 파일 업로드
    private final S3Uploader s3Uploader;

    @Value("${imgUploadLocation}")
    private String imgUploadLocation;

    private final MachineInfoRepository machineInfoRepository;
    private final MachineUsageRepository machineUsageRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final FileUploader fileUploader;

    //운동기구 정보 전체목록 (각 운동기구에 등록되어 있는 영상법 목록도 포함)
    public List<MachineInfoDTO> list() throws Exception {

        List<MachineInfoEntity> machineInfoEntities = machineInfoRepository.findAll();
        List<MachineInfoDTO> machineInfoDTOS = new ArrayList<>();

        for(MachineInfoEntity machineInfoEntity : machineInfoEntities) {
            List<MachineUsageEntity> machineUsageEntities = machineUsageRepository.findAllByMachineInfoEntityOrderByViewCntDesc(machineInfoEntity);
            List<MachineUsageDTO> machineUsageDTOS = new ArrayList<>();

            for(MachineUsageEntity machineUsageEntity : machineUsageEntities) {
                MachineUsageDTO machineUsageDTO = MachineUsageDTO.builder()
                        .id(machineUsageEntity.getId())
                        .machineInfoId(machineUsageEntity.getMachineInfoEntity().getId())
                        .viewCnt(machineUsageEntity.getViewCnt())
                        .title(machineUsageEntity.getTitle())
                        .content(machineUsageEntity.getContent())
                        .url(machineUsageEntity.getUrl())
                        .regDate(machineUsageEntity.getRegDate())
                        .modDate(machineUsageEntity.getModDate())
                        .build();
                machineUsageDTOS.add(machineUsageDTO);
            }

            MachineInfoDTO machineInfoDTO = MachineInfoDTO.builder()
                    .id(machineInfoEntity.getId())
                    .name(machineInfoEntity.getName())
                    .content(machineInfoEntity.getContent())
                    .img(machineInfoEntity.getImg())
                    .result(machineInfoEntity.getResult())
                    .machineUsageDTOList(machineUsageDTOS)
                    .regDate(machineInfoEntity.getRegDate())
                    .modDate(machineInfoEntity.getModDate())
                    .build();

            machineInfoDTOS.add(machineInfoDTO);
        }
        return machineInfoDTOS;
    }

    //운동기구 정보 상세보기
    public MachineInfoDTO detail(Integer id) throws Exception {

        MachineInfoEntity machineInfoEntity = machineInfoRepository.findById(id).orElseThrow();
        MachineInfoDTO machineInfoDTO = modelMapper.map(machineInfoEntity, MachineInfoDTO.class);

        return machineInfoDTO;
    }

    //운동 기구 정보 찾기 (machine_info 테이블의 result 값 중 플라스크 AI 서버로부터 응답받은 class 값이 있는지 조회)
    public MachineInfoDTO find(String className) throws Exception {

        MachineInfoEntity machineInfoEntity = machineInfoRepository.findByResult(className);

        List<MachineUsageDTO> machineUsageDTOS = Arrays.asList(modelMapper
                .map(machineUsageRepository.findAllByMachineInfoEntityOrderByViewCntDesc(machineInfoEntity), MachineUsageDTO[].class));

        MachineInfoDTO machineInfoDTO = modelMapper
                .map(machineInfoRepository.findByResult(className), MachineInfoDTO.class);
        machineInfoDTO.setMachineUsageDTOList(machineUsageDTOS);

        return machineInfoDTO;
    }

    //운동기구 정보 등록
    public void register(MachineInfoDTO machineInfoDTO,
                         MultipartFile imgFile) throws Exception {

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if (originalFileName !=null){
            newFileName = fileUploader.uploadFile(imgUploadLocation, originalFileName, imgFile.getBytes());
        }

        machineInfoDTO.setImg(newFileName);
        MachineInfoEntity machineInfoEntity = modelMapper.map(machineInfoDTO, MachineInfoEntity.class);

        machineInfoRepository.save(machineInfoEntity);
    }

    //운동기구 정보 수정
    public void modify(MachineInfoDTO machineInfoDTO,
                       MultipartFile imgFile) throws Exception {

        //기존파일 삭제
        MachineInfoEntity machineInfoEntity = machineInfoRepository.findById(machineInfoDTO.getId()).orElseThrow();
        String deleteFile = machineInfoEntity.getImg();

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";
        if(originalFileName.length() != 0) {
            if(deleteFile.length() != 0) {
                s3Uploader.deleteFile(deleteFile, imgUploadLocation);
            }

            newFileName = s3Uploader.upload(imgFile, imgUploadLocation);

            machineInfoDTO.setImg((newFileName));
        }
        machineInfoDTO.setId(machineInfoEntity.getId());

        //변환
        MachineInfoEntity data = modelMapper.map(machineInfoDTO, MachineInfoEntity.class);
        machineInfoRepository.save(data);
    }

    public void delete() throws Exception {}

}
