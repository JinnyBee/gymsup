package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @GetMapping("/user_list")
    public String listForm(Model model) throws Exception {
        return "user/list";
    }
    @GetMapping("/user_join")
    public String joinForm(Model model) throws Exception {
        return "user/join";
    }
    @PostMapping("/user_join")
    public String joinProc(Model model) throws Exception {
        return "redirect:/";
    }
    @GetMapping("/user_login")
    public String loginForm(Model model) throws Exception {
        return "user/login";
    }
    @GetMapping("/user_detail")
    public String detailForm(Model model) throws Exception {
        return "user/detail";
    }
    @GetMapping("/user_modify")
    public String modifyForm(Model model) throws Exception {
        return "user/modify";
    }
    @PostMapping("/user_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_cancel")
    public String cancelProc(Model model) throws Exception {
        return "redirect:/";
    }
    @GetMapping("/user_mywrite")
    public String myWrite(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_mycomment")
    public String myComment(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_mybookmark")
    public String myBookmark(Model model) throws Exception {
        return "redirect:/user/detail";
    }
}