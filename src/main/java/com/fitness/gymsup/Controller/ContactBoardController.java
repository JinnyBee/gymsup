package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactBoardController {
    @GetMapping("/board_contact_register")
    public String registerForm(Model model) throws Exception {
        return "board/contact/register";
    }
    @PostMapping("/board_contact_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/board/contact/list";
    }
    @GetMapping("/board_contact_list")
    public String listForm(Model model) throws Exception {
        return "board/contact/list";
    }
    @GetMapping("/board_contact_detail")
    public String detailForm(Model model) throws Exception {
        return "board/contact/detail";
    }
    @GetMapping("/board_contact_remove")
    public String removeProc(Model model) throws Exception {
        return "board/contact/remove";
    }
    @PostMapping("/board_contact_commentregister")
    public String commentRegisterProc(Model model) throws Exception {
        return "redirect:/board/diary/detail";
    }
    @GetMapping("/board_contact_commentremove")
    public String commentRemoveProc(Model model) throws Exception {
        return "redirect:/board/diary/detail";
    }
}