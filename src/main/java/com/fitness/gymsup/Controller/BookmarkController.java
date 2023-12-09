/*
    파일명 : BookmarkController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.DTO.BookmarkDTO;
import com.fitness.gymsup.Service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Log4j2
public class BookmarkController extends BoardBaseController {
    private final BookmarkService bookmarkService;

    //마이페이지 - 내 북마크 게시글 전체 목록
    @GetMapping("/bookmark_list")
    private String listForm(@PageableDefault(page = 1) Pageable pageable,
                            Model model,
                            HttpServletRequest request,
                            Principal principal) throws Exception {

        Page<BookmarkDTO> bookmarkDTOS = bookmarkService.list(BookmarkType.BOOKMARK, pageable, request, principal);
        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(bookmarkDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            endPage = ((startPage+blockLimit-1)<bookmarkDTOS.getTotalPages()) ? startPage+blockLimit-1 : bookmarkDTOS.getTotalPages();

            prevPage = bookmarkDTOS.getNumber();
            currentPage = bookmarkDTOS.getNumber() + 1;
            nextPage = bookmarkDTOS.getNumber() + 2;
            lastPage = bookmarkDTOS.getTotalPages();
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("bookmarkDTOS",bookmarkDTOS);

        return "user/bookmark";
    }

    //북마크 설정 처리
    @GetMapping("/bookmark_on")
    private String bookmarkOnProc(@RequestParam("id") Integer bid,
                                  @RequestParam("categoryType") String categoryType,
                                  HttpServletRequest request,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) throws Exception {

        log.info(bid + ", " + categoryType);
        bookmarkService.register(bid, BookmarkType.BOOKMARK, request, principal);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:" + getRedirectUrl(categoryType);
    }

    //북마크 해제 처리
    @GetMapping("/bookmark_off")
    private String bookmarkOffProc(@RequestParam("id") Integer bid,
                                   @RequestParam("categoryType") String categoryType,
                                   HttpServletRequest request,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) throws Exception {

        log.info(bid + ", " + categoryType);
        bookmarkService.delete(bid, BookmarkType.BOOKMARK, request, principal);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:" + getRedirectUrl(categoryType);
    }

    //좋아요 설정 처리
    @GetMapping("/good_on")
    private String goodOnProc(@RequestParam("id") Integer bid,
                              @RequestParam("categoryType") String categoryType,
                              HttpServletRequest request,
                              Principal principal,
                              RedirectAttributes redirectAttributes) throws Exception {

        log.info(bid + ", " + categoryType);
        bookmarkService.register(bid, BookmarkType.GOOD, request, principal);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:" + getRedirectUrl(categoryType);
    }
}