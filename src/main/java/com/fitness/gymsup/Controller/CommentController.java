package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment_register")
    public String registerProc(@Valid CommentDTO commentDTO,
                               BindingResult bindingResult,
                               String categoryType,
                               RedirectAttributes redirectAttributes) throws Exception {
        log.info("boardId : " + commentDTO.getBoardId() + ", userId : " + commentDTO.getUserId() + "categoryType : " + categoryType);
        if (bindingResult.hasErrors()) {
            log.info(getRedirectUrl(categoryType));
            return "redirect:" + getRedirectUrl(categoryType);
        }

        commentService.register(commentDTO);
        redirectAttributes.addAttribute("id", commentDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }

    @GetMapping("/comment_remove")
    public String removeProc(int bid,
                             int id,
                             RedirectAttributes redirectAttributes) throws Exception {
        commentService.remove(id);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:/board_diary_detail";
    }

    private String getRedirectUrl(String categoryType) {
        String redirectUrl = "";
        if (categoryType.equals(BoardCategoryType.BTYPE_NOTIFY.name())) {
            redirectUrl = "/board_notify_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_TIP.name())) {
            redirectUrl = "/board_tip_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_DIARY.name())) {
            redirectUrl = "/board_diary_detail";
        } else if (categoryType.equals(BoardCategoryType.BTYPE_QNA.name())) {
            redirectUrl = "/board_qna_detail";
        } else {
            redirectUrl = "/";
        }
        return redirectUrl;
    }
}
