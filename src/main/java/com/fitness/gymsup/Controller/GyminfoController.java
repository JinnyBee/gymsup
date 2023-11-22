package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GyminfoController {
    @GetMapping("/gyminfo_about")
    public String aboutForm(Model model) throws Exception {
        return "gyminfo/about";
    }
    @GetMapping("/gyminfo_trainer")
    public String trainerForm(Model model) throws Exception {
        return "gyminfo/trainer";
    }
    @GetMapping("/gyminfo_program")
    public String programForm(Model model) throws Exception {
        return "gyminfo/program";
    }
    @GetMapping("/gyminfo_interior")
    public String interiorForm(Model model) throws Exception {
        return "gyminfo/interior";
    }
    @GetMapping("/gyminfo_location")
    public String locationForm(Model model) throws Exception {
        return "gyminfo/location";
    }
    @GetMapping("/gyminfo_list")
    public String listForm(Model model) throws Exception {
        return "gyminfo/list";
    }
    @GetMapping("/gyminfo_register")
    public String registerForm(Model model) throws Exception {
        return "gyminfo/register";
    }
    @PostMapping("/gyminfo_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/gyminfo_list";
    }
    @GetMapping("/gyminfo_detail")
    public String detailForm(Model model) throws Exception {
        return "gyminfo/detail";
    }
    @GetMapping("/gyminfo_modify")
    public String modifyForm(Model model) throws Exception {
        return "gyminfo/modify";
    }
    @PostMapping("/gyminfo_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/gyminfo_list";
    }
    @GetMapping("/gyminfo_remove")
    public String removeProc(Model model) throws Exception {
        return "gyminfo_remove";
    }
}