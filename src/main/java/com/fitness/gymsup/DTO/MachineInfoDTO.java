package com.fitness.gymsup.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineInfoDTO {
    private int id;
    private String name;
    private String content;
    private String img;
    private String result;
    private LocalDateTime regDate;              //생성일
    private LocalDateTime modDate;              //수정일
}
