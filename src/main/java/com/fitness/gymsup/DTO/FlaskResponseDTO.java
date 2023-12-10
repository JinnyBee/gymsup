package com.fitness.gymsup.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class FlaskResponseDTO {
    private String resultFilename;  //결과 이미지 파일
    private List<String> name;      //식별된 클래스 이름
}