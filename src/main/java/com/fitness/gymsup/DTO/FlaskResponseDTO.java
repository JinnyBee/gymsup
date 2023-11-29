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
    private String resultFilename;
    private List<String> name;
}
