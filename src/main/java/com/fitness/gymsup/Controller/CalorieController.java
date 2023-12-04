package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.FoodCalorieDTO;
import com.fitness.gymsup.Service.CalorieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CalorieController {
    private final CalorieService calorieService;

    @GetMapping("/mybmi")
    public String myBmiForm(Model model) throws Exception {
        return "calorie/mybmi";
    }
    @GetMapping("/food_calorie_calc")
    public String foodCalorieCalcForm(Model model) throws Exception {
        log.info("foodCalorieCalcForm");
        return "calorie/food_calc";
    }
    @GetMapping("/food_calorie_search")
    public String foodCalorieSearch(String keyword,
                                    Model model) throws Exception {
        log.info("keyword : " + keyword);
        List<FoodCalorieDTO> foodCalorieDTOS = calorieService.requestToFoodDB(keyword);
        model.addAttribute("foodCalorieDTOS", foodCalorieDTOS);

        return "calorie/food_list";
    }
    @PostMapping("/food_calorie_detail")
    public String foodCalorieDetail(FoodCalorieDTO foodCalorieDTO,
                                    Model model) throws Exception {
        log.info(foodCalorieDTO);
        model.addAttribute("foodCalorieDTO", foodCalorieDTO);
        return "calorie/food_detail";
    }
    @GetMapping(value = "/food_calorie_call")
    public String callFoodApi(String keyword,
                              Model model) throws Exception {
        StringBuffer result = new StringBuffer();
        Integer startIdx = 1;
        Integer endIdx = 10;

        try {
            String urlstr = "http://openapi.foodsafetykorea.go.kr/api/be61650aef44488a9029/I2790"
                          + "/JSON/"
                          + Integer.toString(startIdx) + "/"
                          + Integer.toString(endIdx) + "/"
                          + "DESC_KOR=" + keyword;

            log.info("REQ : " + urlstr);

            URL url = new URL(urlstr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;

            while((returnLine = br.readLine()) != null) {
                log.info(returnLine);
                result.append(returnLine);
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            log.info(e.toString());
        }
        log.info("RES : " + result.toString());

        return result.toString();
    }
    @GetMapping("/calorie_food_detail")
    public String foodDetailForm(Model model) throws Exception {
        return "calorie/food_detail";
    }
    @GetMapping("/calorie_exercise_list")
    public String exerciseListForm(Model model) throws Exception {
        return "calorie/exercise_list";
    }
    @GetMapping("/calorie_exercise_detail")
    public String exerciseDetailForm(Model model) throws Exception {
        return "calorie/exercise_detail";
    }
}