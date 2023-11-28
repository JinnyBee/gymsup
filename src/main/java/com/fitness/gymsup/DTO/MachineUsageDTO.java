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

    private int id;
    @NotEmpty(message = "제목을 적어주세요.")
    private String title;
    private Integer machineInfoId;
    private String machineInfoName;

    @NotEmpty(message = "내용을 적어주세요.")
    private String content;
    @NotEmpty(message = "src주소를 적어주세요.")
    private String url;
    private String thumbnail;
    private Integer viewCnt;

    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}
