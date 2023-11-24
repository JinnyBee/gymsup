package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.BookmarkType;
import com.fitness.gymsup.DTO.BookmarkDTO;
import com.fitness.gymsup.Entity.BookmarkEntity;
import com.fitness.gymsup.Service.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j

public class BookmarkController extends BaseController {
    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark_list")
    private String listForm(@PageableDefault(page = 1) Pageable pageable,
                            Model model,
                            HttpServletRequest request,
                            Principal principal) throws Exception{
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
            //endPage = Math.min(startPage+blockLimit-1, boardDTOS.getTotalPages());
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

    @PostMapping("/bookmark_register")
    private String registerProc(BookmarkDTO bookmarkDTO,
                                HttpServletRequest request,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                String categoryType) throws Exception{

        bookmarkDTO.setBookmarkType(BookmarkType.BOOKMARK);
        bookmarkService.register(bookmarkDTO, request, principal);

        redirectAttributes.addAttribute("id", bookmarkDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }
}
