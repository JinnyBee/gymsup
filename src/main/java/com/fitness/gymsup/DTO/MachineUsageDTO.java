package com.fitness.gymsup.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MachineUsageDTO {

    private int id;                             //운동 기구 영상 번호

    @NotEmpty(message = "제목을 적어주세요.")
    private String title;                       //운동 기구 영상 제목

    private Integer machineInfoId;              //운동 기구 번호
    private String machineInfoName;             //운동 기구 이름

    @NotEmpty(message = "내용을 적어주세요.")
    private String content;                     //운동 가구 영상 설명
    @NotEmpty(message = "src주소를 적어주세요.")
    private String url;                         //운동 기구 영상 주소

    private String thumbnail;                   //운동 기구 영상 썸네일
    private Integer viewCnt;                    //운동 기구 조회수

    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}
