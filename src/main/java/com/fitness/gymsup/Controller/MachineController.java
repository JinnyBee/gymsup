package com.fitness.gymsup.Controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Log4j2
public class MachineController {
    @GetMapping("/machine_detect")
    public String detectForm(Model model) throws Exception {
        return "machine/detect";
    }
    @GetMapping("/machine_howto")
    public String howtoProc(Model model) throws Exception {
        return "machine/howto";
    }
    @GetMapping("/machine_about")
    public String aboutForm(Model model) throws Exception {
        return "machine/about";
    }
    @GetMapping("/machine_detail")
    public String detailForm(Model model) throws Exception {
        return "machine/detail";
    }
    @GetMapping("/machine_list")
    public String listForm(Model model) throws Exception {
        return "machine/list";
    }
    @GetMapping("/machine_register")
    public String registerForm(Model model) throws Exception {
        return "machine/register";
    }
    @PostMapping("/machine_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/machine/list";
    }
    @GetMapping("/machine_modify")
    public String modifyForm(Model model) throws Exception {
        return "machine/modify";
    }
    @PostMapping("/machine_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/machine/list";
    }
    @GetMapping("/machine_remove")
    public String removeProc(Model model) throws Exception {
        return "machine/remove";
    }
}