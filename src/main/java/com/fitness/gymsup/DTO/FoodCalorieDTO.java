package com.fitness.gymsup.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodCalorieDTO {
    private String foodCd;          //FOOD_CD       : 식품코드
    private String groupName;       //GROUP_NAME    : 식품군
    private String descKor;         //DESC_KOR      : 식품이름

    private String researchYear;    //RESEARCH_YEAR : 조사년도
    private String makerName;       //MAKER_NAME    : 제조사명
    private String subRefName;      //SUB_REF_NAME  : 자료출처

    private String servingSize;     //SERVING_SIZE  : 총내용량
    private String servingUnit;     //SERVING_UNIT  : 총내용량단위

    private String nutrCont1;       //NUTR_CONT1    : 열량(kcal)(1회제공량당)
    private String nutrCont2;       //NUTR_CONT2    : 탄수화물(g)(1회제공량당)
    private String nutrCont3;       //NUTR_CONT3    : 단백질(g)(1회제공량당)
    private String nutrCont4;       //NUTR_CONT4    : 지방(g)(1회제공량당)
    private String nutrCont5;       //NUTR_CONT5    : 당류(g)(1회제공량당)
    private String nutrCont6;       //NUTR_CONT6    : 나트륨(mg)(1회제공량당)
    private String nutrCont7;       //NUTR_CONT7    : 콜레스테롤(mg)(1회제공량당)
    private String nutrCont8;       //NUTR_CONT8    : 포화지방산(g)(1회제공량당)
    private String nutrCont9;       //NUTR_CONT9    : 트랜스지방(g)(1회제공량당)
}
