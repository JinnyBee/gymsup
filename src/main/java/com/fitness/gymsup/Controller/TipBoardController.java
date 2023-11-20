package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TipBoardController {
    @GetMapping("/board_tip_list")
    public String listForm(Model model) throws Exception {
        return "board/tip/list";
    }
    @GetMapping("/board_tip_register")
    public String registerForm(Model model) throws Exception {
        return "board/tip/register";
    }
    @PostMapping("/board_tip_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/board/tip/list";
    }
    @GetMapping("/board_tip_detail")
    public String detailForm(Model model) throws Exception {
        return "board/tip/detail";
    }
    @GetMapping("/board_tip_goodcnt")
    public String goodcntProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @GetMapping("/board_tip_bookmarkon")
    public String bookmarkOnProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @GetMapping("/board_tip_bookmarkoff")
    public String bookmarkOff(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @GetMapping("/board_tip_modify")
    public String modifyForm(Model model) throws Exception {
        return "board/tip/modify";
    }
    @PostMapping("/board_tip_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/board/tip/list";
    }
    @GetMapping("/board_tip_remove")
    public String removeProc(Model model) throws Exception {
        return "board/tip/remove";
    }
    @PostMapping("/board_tip_commentregister")
    public String commentRegisterProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @GetMapping("/board_tip_commentremove")
    public String commentRemoveProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @PostMapping("/board_tip_replyregister")
    public String replyRegisterProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
    @GetMapping("/board_tip_replyremove")
    public String replyRemoveProc(Model model) throws Exception {
        return "redirect:/board/tip/detail";
    }
}