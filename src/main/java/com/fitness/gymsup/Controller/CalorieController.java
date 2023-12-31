/*
    파일명 : CalorieController.java
    기 능 : BMI 계산, 음식칼로리 검색, 운동칼로리 검색
    작성일 : 2023.12.08
    작성자 : 전현진, 김진영
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.BmiDTO;
import com.fitness.gymsup.DTO.ExerciseDTO;
import com.fitness.gymsup.DTO.FoodCalorieDTO;
import com.fitness.gymsup.Service.CalorieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CalorieController {
    private final CalorieService calorieService;

    //BMI계산 폼
    @GetMapping("/mybmi_calc")
    public String myBmiForm(Model model) throws Exception {

        return "calorie/mybmi_calc";
    }

    //BMI계산 처리
    @PostMapping("/mybmi_calc")
    public String myBmiProc(BmiDTO bmiDTO, Model model) throws Exception {

        double weight = bmiDTO.getWeight();
        double height = bmiDTO.getHeight()/100;
        double bmi = weight/(height*height);
        String status = "";

        log.info("weight:"+weight+", height:"+height+"--->bmi:"+bmi);

        if(bmi < 18.5) {
            status = "저체중";
        } else if(bmi >= 18.5 && bmi < 23) {
            status = "정상";
        } else if(bmi >= 23 && bmi < 25) {
            status = "과체중";
        } else if(bmi >= 25 && bmi < 30) {
            status = "비만";
        } else if(bmi >= 30 && bmi < 35) {
            status = "고도비만";
        } else {
            status = "초고도비만";
        }

        bmiDTO.setMyBMI(Math.round(bmi*100)/100);
        bmiDTO.setWeightStatus(status);

        model.addAttribute("bmiDTO", bmiDTO);
        log.info(bmiDTO);

        return "calorie/mybmi_detail";
    }

    //음식칼로리 검색 폼
    @GetMapping("/food_calorie_calc")
    public String foodCalorieCalcForm(Model model) throws Exception {

        log.info("foodCalorieCalcForm");
        return "calorie/food_calc";
    }

    //음식칼로리 검색 처리
    @GetMapping("/food_calorie_search")
    public String foodCalorieSearch(String keyword,
                                    Model model) throws Exception {
        log.info("keyword : " + keyword);

        List<FoodCalorieDTO> foodCalorieDTOS = calorieService.requestToFoodDB(keyword);

        model.addAttribute("keyword", keyword);
        model.addAttribute("foodCalorieDTOS", foodCalorieDTOS);

        return "calorie/food_list";
    }

    //음식칼로리 상세보기
    @GetMapping("/food_calorie_detail")
    public String foodCalorieDetail(String keyword,
                                    FoodCalorieDTO foodCalorieDTO,
                                    Model model) throws Exception {

        log.info(keyword);
        log.info(foodCalorieDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("foodCalorieDTO", foodCalorieDTO);

        return "calorie/food_detail";
    }

    //운동칼로리 검색 폼 및 결과
    @GetMapping("/exercise_calorie_calc")
    public String exerciseCalorieCalcForm(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "keyword", defaultValue = "")String keyword,
                                          Model model) throws Exception {

        Page<ExerciseDTO> exerciseDTOS = calorieService.list(page, keyword);

        model.addAttribute("keyword", keyword);                 // 검색 키워드
        model.addAttribute("exerciseDTOS", exerciseDTOS);       // 데이터
        return "calorie/exercise_calc";
    }
}