package com.fitness.gymsup.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineInfoDTO {
    private int id;                                     //운동 기구 번호
    private String name;                                //운동 기구 이름
    private String content;                             //운동 기구 설명
    private String img;                                 //운동 기구 이미지
    private String result;                              //운동 기구 감지한 결과
    private List<MachineUsageDTO> machineUsageDTOList;
    private LocalDateTime regDate;                      //생성일
    private LocalDateTime modDate;                      //수정일
}
