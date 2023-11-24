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
public class CommentController extends BaseController {
    private final CommentService commentService;
    private final ReplyService replyService;

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

    @PostMapping("/reply_register")
    public String registerReplyProc(@Valid ReplyDTO replyDTO,
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
    public String removeReplyProc(int bid,
                                  int id,
                                  RedirectAttributes redirectAttributes) throws Exception {
        replyService.remove(id);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:/board_diary_detail";
    }
}
