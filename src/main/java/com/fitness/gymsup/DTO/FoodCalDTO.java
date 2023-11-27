package com.fitness.gymsup.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodCalDTO {
    private Integer id;

    private String makerName;
    private String descKor;
    private String servingSize;
    private String servingUnit;
    private String nutrCont1;
}
