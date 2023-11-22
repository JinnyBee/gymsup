package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.Service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class TipBoardController {
    private final BoardService boardService;

    @GetMapping("/board_tip_list")
    public String listForm(@PageableDefault(page = 1) Pageable pageable,
                           Model model) throws Exception {
        Page<BoardDTO> boardDTOS = boardService.list(BoardCategoryType.BTYPE_DIARY, pageable);
        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(boardDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, boardDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<boardDTOS.getTotalPages()) ? startPage+blockLimit-1 : boardDTOS.getTotalPages();

            prevPage = boardDTOS.getNumber();
            currentPage = boardDTOS.getNumber() + 1;
            nextPage = boardDTOS.getNumber() + 2;
            lastPage = boardDTOS.getTotalPages();
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        log.info("getTotalPages : " + boardDTOS.getTotalPages());
        log.info("startPage : "+startPage);
        log.info("endPage : "+endPage);
        log.info("prevPage : "+prevPage);
        log.info("currentPage : "+currentPage);
        log.info("nextPage : "+nextPage);
        log.info("lastPage : "+lastPage);

        model.addAttribute("boardDTOS", boardDTOS);

        return "board/tip/list";
    }
    @GetMapping("/board_tip_register")
    public String registerForm(Model model) throws Exception {
        BoardDTO boardDTO = new BoardDTO();
        model.addAttribute("boardDTO", boardDTO);

        return "board/tip/register";
    }
    @PostMapping("/board_tip_register")
    public String registerProc(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               List<MultipartFile> imgFiles,
                               Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            return "board/tip/register";
        }
        boardService.register(boardDTO, imgFiles);
        return "redirect:/board/tip/list";
    }
    @GetMapping("/board_tip_detail")
    public String detailForm(Integer id, Model model) throws Exception {
        BoardDTO boardDTO = boardService.detail(id, "R");
        model.addAttribute("boardDTO", boardDTO);

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
    public String modifyForm(Integer id,
                             Model model) throws Exception {
        BoardDTO boardDTO = boardService.detail(id, "");
        model.addAttribute("boardDTO", boardDTO);

        return "board/tip/modify";
    }
    @PostMapping("/board_tip_modify")
    public String modifyProc(@Valid BoardDTO boardDTO,
                             BindingResult bindingResult,
                             Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            return "board/tip/modify";
        }
        boardService.modify(boardDTO);
        return "redirect:/board/tip/list";
    }
    @GetMapping("/board_tip_remove")
    public String removeProc(Integer id,
                             Model model) throws Exception {
        boardService.remove(id);
        return "redirect:/board_tip_list";
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