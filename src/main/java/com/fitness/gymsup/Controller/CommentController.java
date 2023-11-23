package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment_register")
    public String registerProc(CommentDTO commentDTO,
                               String categoryType,
                               BoardCategoryType boardCategoryType,
                               RedirectAttributes redirectAttributes) throws Exception {
        log.info("boardId : " + commentDTO.getBoardId() + ", userId : " + commentDTO.getUserId());
        log.info("categoryType : " + categoryType);

        commentService.register(commentDTO);
        redirectAttributes.addAttribute("id", commentDTO.getBoardId());

        if(categoryType.equals(BoardCategoryType.BTYPE_NOTIFY.name())) {
            return "redirect:/board_notify_detail";
        } else if(categoryType.equals(BoardCategoryType.BTYPE_TIP.name())) {
            return "redirect:/board_tip_detail";
        } else if(categoryType.equals(BoardCategoryType.BTYPE_DIARY.name())) {
            return "redirect:/board_diary_detail";
        } else if(categoryType.equals(BoardCategoryType.BTYPE_QNA.name())) {
            return "redirect:/board_qna_detail";
        }

        return "redirect:/";
    }

    @GetMapping("/comment_remove")
    public String removeProc(int bid,
                             int id,
                             RedirectAttributes redirectAttributes) throws Exception {
        commentService.remove(id);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:/board_diary_detail";
    }
}
