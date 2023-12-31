/*
    파일명 : AllBoardController.java
    기 능 : 전체게시판 전체목록/상세보기
    작성일 : 2023.12.08
    작성자 : 김진영
*/
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AllBoardController extends BoardBaseController {
    private final BoardService boardService;

    //모든게시판 전체목록 조회
    @GetMapping("/board_list")
    public String listForm(@PageableDefault(page = 1) Pageable pageable,
                           Model model) throws Exception {

        List<BoardDTO> notifyBoardLatestDTOS = boardService.latest(BoardCategoryType.BTYPE_NOTIFY);
        Page<BoardDTO> boardDTOS = boardService.listAllWithoutCategory(pageable, BoardCategoryType.BTYPE_NOTIFY);

        int blockLimit = 10;
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
        for(BoardDTO dto : notifyBoardLatestDTOS) {
            log.info("Notify board" + dto);
        }
        for(BoardDTO dto : boardDTOS) {
            log.info("Without Notify board" + dto);
        }

        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_ALL.getDescription());
        model.addAttribute("notifyBoardLatestDTOS", notifyBoardLatestDTOS);
        model.addAttribute("boardDTOS", boardDTOS);

        return "board/list";
    }

    //전체게시판에서 상세조회 이동
    @GetMapping("/board_detail")
    public String detailForm(Integer id,
                             String categoryType,
                             Integer page,
                             RedirectAttributes redirectAttributes,
                             Model model) throws Exception {

        log.info(categoryType);
        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addAttribute("page",1);
        return "redirect:" + getDetailRedirectUrl(categoryType);
    }
}