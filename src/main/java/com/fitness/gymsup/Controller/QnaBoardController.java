package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class QnaBoardController {
    @GetMapping("/board_qna_list")
    public String listForm(Model model) throws Exception {
        return "board/qna/list";
    }
    @GetMapping("/board_qna_register")
    public String registerForm(Model model) throws Exception {
        return "board/qna/register";
    }
    @PostMapping("/board_qna_register")
    public String registerProc(Model model) throws Exception {
        return "redirect:/board/qna/list";
    }
    @GetMapping("/board_qna_detail")
    public String detailForm(Model model) throws Exception {
        return "board/qna/detail";
    }
    @GetMapping("/board_qna_goodcnt")
    public String goodcntProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @GetMapping("/board_qna_bookmarkon")
    public String bookmarkOnProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @GetMapping("/board_qna_bookmarkoff")
    public String bookmarkOffProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @GetMapping("/board_qna_modify")
    public String modifyForm(Model model) throws Exception {
        return "board/qna/modify";
    }
    @PostMapping("/board_qna_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/board/qna/list";
    }
    @GetMapping("/board_qna_remove")
    public String removeProc(Model model) throws Exception {
        return "board/qna/remove";
    }
    @PostMapping("/board_qna_commentregister")
    public String commentRegisterProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @GetMapping("/board_qna_commentremove")
    public String commentRemoveProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @PostMapping("/board_qna_replyregister")
    public String replyRegisterProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
    @GetMapping("/board_qna_replyremove")
    public String replyRemoveProc(Model model) throws Exception {
        return "redirect:/board/qna/detail";
    }
}