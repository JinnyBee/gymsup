package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.Service.BoardService;
import com.fitness.gymsup.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class FreeBoardController {
    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/board_free_list")
    public String listForm(@PageableDefault(page = 1) Pageable pageable,
                           @RequestParam(value = "type", defaultValue = "") String type,
                           @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           Model model) throws Exception {

        Page<BoardDTO> boardDTOS = boardService.list(pageable, BoardCategoryType.BTYPE_FREE, type, keyword);
        List<BoardDTO> notifyBoardLatestDTOS = boardService.latest(BoardCategoryType.BTYPE_NOTIFY);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if (boardDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, boardDTOS.getTotalPages());
            endPage = ((startPage + blockLimit - 1) < boardDTOS.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOS.getTotalPages();

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
        log.info("startPage : " + startPage);
        log.info("endPage : " + endPage);
        log.info("prevPage : " + prevPage);
        log.info("currentPage : " + currentPage);
        log.info("nextPage : " + nextPage);
        log.info("lastPage : " + lastPage);

        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_FREE.getDescription());
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("notifyBoardLatestDTOS", notifyBoardLatestDTOS);
        model.addAttribute("boardDTOS", boardDTOS);

        for (BoardDTO dto : boardDTOS) {
            log.info(dto);
        }

        return "board/free/list";
    }

    @GetMapping("/board_free_register")
    public String registerForm(Model model) throws Exception {

        BoardDTO boardDTO = new BoardDTO();

        boardDTO.setCategoryType(BoardCategoryType.BTYPE_FREE);
        log.info(boardDTO.getCategoryType().name());
        log.info(boardDTO.getCategoryType().getDescription());

        model.addAttribute("boardDTO", boardDTO);

        return "board/free/register";
    }

    @PostMapping("/board_free_register")
    public String registerProc(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               List<MultipartFile> imgFiles,
                               Principal principal,
                               HttpServletRequest request,
                               Model model) throws Exception {

        log.info(boardDTO.getCategoryType().name());
        for (MultipartFile imgFile : imgFiles) {
            log.info(imgFile);
        }
        if (bindingResult.hasErrors()) {
            return "board/free/register";
        }
        boardService.register(boardDTO, imgFiles, request, principal);

        return "redirect:/board_free_list";
    }

    @GetMapping("/board_free_detail")
    public String detailForm(Integer id,
                             HttpServletRequest request,
                             Principal principal,
                             int page,
                             Model model) throws Exception {

        //로그인 user id 조회
        Integer loginUserId = boardService.userId(request, principal);
        //해당게시글 상세조회
        BoardDTO boardDTO = boardService.detail(id, true, request, principal);
        //댓글목록 조회
        List<CommentDTO> commentDTOS = commentService.list(id);
        boardDTO.setCommentCount(commentDTOS.size());

        log.info(boardDTO);
        log.info(commentDTOS);

        /*Integer userId = boardService.userId(request, principal);

        model.addAttribute("userId", userId);*/
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_FREE.getDescription());
        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("page",page);

        return "board/free/detail";
    }

    @GetMapping("/board_free_reload")
    public String reloadForm(Integer id,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {

        //로그인 user id 조회
        Integer loginUserId = boardService.userId(request, principal);
        //해당게시글 상세조회 Reload
        BoardDTO boardDTO = boardService.detail(id, false, request, principal);
        //댓글목록 조회
        List<CommentDTO> commentDTOS = commentService.list(id);

        log.info(boardDTO);
        log.info(commentDTOS);

        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_FREE.getDescription());
        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "board/free/detail";
    }

    @GetMapping("/board_free_modify")
    public String modifyForm(Integer id,
                             Integer boardUserId,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {

        if(boardUserId != commentService.userId(request, principal) ) {
            return "redirect:/";
        }

        BoardDTO boardDTO = boardService.detail(id, false, request, principal);

        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "board/free/modify";
    }

    @PostMapping("/board_free_modify")
    public String modifyProc(@Valid BoardDTO boardDTO,
                             BindingResult bindingResult,
                             List<MultipartFile> imgFiles,
                             Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "board/free/modify";
        }
        boardService.modify(boardDTO, imgFiles);

        return "redirect:/board_free_list";
    }

    @GetMapping("/board_free_delete")
    public String deleteProc(Integer id,
                             Integer boardUserId,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {

        if(boardUserId != commentService.userId(request, principal) ) {
            return "redirect:/";
        }
        boardService.delete(id);

        return "redirect:/board_free_list";
    }
}
