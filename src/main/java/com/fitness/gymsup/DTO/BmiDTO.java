package com.fitness.gymsup.DTO;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.BookmarkType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BmiDTO {
    private Integer height;         //신장 (cm)
    private Integer weight;         //체중 (kg)
    private double myBMI;           //체질량 지수 (BMI)
    private String weightStatus;    //비만도 결과 (저체중, 정상, 과체중, 비만)
}