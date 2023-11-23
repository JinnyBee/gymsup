package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Service.CommentService;
import com.fitness.gymsup.Service.ReplyService;
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
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping("/reply_register")
    public String registerProc(@Valid ReplyDTO replyDTO,
                               BindingResult bindingResult,
                               String categoryType,
                               RedirectAttributes redirectAttributes) throws Exception {
        log.info("commentId : " + replyDTO.getCommentId() + ", userId : " + replyDTO.getUserId() + "categoryType : " + categoryType);
        if (bindingResult.hasErrors()) {
            log.info(getRedirectUrl(categoryType));
            return "redirect:" + getRedirectUrl(categoryType);
        }

        replyService.register(replyDTO);
        redirectAttributes.addAttribute("id", replyDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }

    @GetMapping("/reply_remove")
    public String removeProc(int bid,
                             int id,
                             RedirectAttributes redirectAttributes) throws Exception {
        replyService.remove(id);
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
