package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CalorieController {
    @GetMapping("/mybmi")
    public String myBmiForm(Model model) throws Exception {
        return "calorie/mybmi";
    }
    @GetMapping("/calorie_food_list")
    public String foodListForm(Model model) throws Exception {
        return "calorie/food_list";
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