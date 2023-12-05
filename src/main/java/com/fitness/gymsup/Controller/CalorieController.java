package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.BmiDTO;
import com.fitness.gymsup.DTO.FoodCalorieDTO;
import com.fitness.gymsup.Service.CalorieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/mybmi_calc")
    public String myBmiForm(Model model) throws Exception {
        return "calorie/mybmi_calc";
    }

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
    @GetMapping("/food_calorie_detail")
    public String foodCalorieDetail(FoodCalorieDTO foodCalorieDTO,
                                    Model model) throws Exception {
        log.info(foodCalorieDTO);
        model.addAttribute("foodCalorieDTO", foodCalorieDTO);
        return "calorie/food_detail";
    }
    /*@GetMapping(value = "/food_calorie_call")
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
    }*/
    @GetMapping("/myfood_calorie_main")
    public String myfoodCalorieMain(Model model) throws Exception {

        return "calorie/myfood_main";
    }
    @GetMapping("/myfood_calorie_calc")
    public String myfoodCalorieCalcForm(Model model) throws Exception {

        return "calorie/myfood_calc";
    }
    @GetMapping("/myfood_calorie_search")
    public String myfoodCalorieSearch(String keyword,
                                      Model model) throws Exception {
        log.info("keyword : " + keyword);
        List<FoodCalorieDTO> myfoodCalorieDTOS = calorieService.requestToFoodDB(keyword);

        model.addAttribute("keyword", keyword);
        model.addAttribute("myfoodCalorieDTOS", myfoodCalorieDTOS);

        return "calorie/myfood_list";
    }
    @GetMapping("/myfood_calorie_detail")
    public String myfoodCalorieDetail(FoodCalorieDTO foodCalorieDTO,
                                      Model model) throws Exception {
        log.info(foodCalorieDTO);
        model.addAttribute("foodCalorieDTO", foodCalorieDTO);
        return "calorie/myfood_detail";
    }
    @GetMapping("/exercise_calorie_calc")
    public String exerciseCalorieCalcForm(Model model) throws Exception {
        return "calorie/exercise_calc";
    }
    @GetMapping("/exercise_calorie_search")
    public String exerciseCalorieSearch(String keyword,
                                        Model model) throws Exception {
        return "calorie/exercise_list";
    }
    @GetMapping("/exercise_calorie_detail")
    public String exerciseCalorieDetail(FoodCalorieDTO foodCalorieDTO,
                                        Model model) throws Exception {
        return "calorie/exercise_detail";
    }
}