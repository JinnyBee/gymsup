package com.fitness.gymsup.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseDTO {
    private Integer id;
    private String exercisename;
    private Integer minute;
    private Integer kcal;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
