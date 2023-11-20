package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotifyBoardController {
    @GetMapping("/board_list")
    public String listAllForm(Model model) throws Exception {
        return "board/list";
    }
    @GetMapping("/board_notify_list")
    public String listForm(Model model) throws Exception {
        return "board/notify/list";
    }
    @GetMapping("/board_notify_register")
    public String registerForm(Model model) throws Exception {
        return "board/notify/register";
    }
    @PostMapping("/board_notify_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/board/notify/list";
    }
    @GetMapping("/board_notify_detail")
    public String detailForm(Model model) throws Exception {
        return "board/notify/detail";
    }
    @GetMapping("/board_notify_goodcnt")
    public String goodCntProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @GetMapping("/board_notify_bookmarkon")
    public String bookmarkOnProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @GetMapping("/board_notify_bookmarkoff")
    public String bookmarkOffProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @GetMapping("/board_notify_modify")
    public String modifyForm(Model model) throws Exception {
        return "board/notify/modify";
    }
    @PostMapping("/board_notify_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/board/notify/list";
    }
    @GetMapping("/board_notify_remove")
    public String removeProc(Model model) throws Exception {
        return "board/notify/remove";
    }
    @PostMapping("/board_notify_commentregister")
    public String commentRegisterProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @GetMapping("/board_notify_commentremove")
    public String commentRemoveProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @PostMapping("/board_notify_replyregister")
    public String replyRegisterProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
    @GetMapping("/board_notify_replyremove")
    public String replyRemoveProc(Model model) throws Exception {
        return "redirect:/board/notify/detail";
    }
}