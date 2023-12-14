/*
    파일명 : NotifyBoardController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class NotifyBoardController extends BoardBaseController {
    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/board_notify_list")
    public String listForm(String Url) throws Exception {
        return "redirect:" + Url;
    }


    @GetMapping("/board_notify_detail")
    public String detailForm(Integer id,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {

        //로그인 user id 조회
        Integer loginUserId = boardService.userId(request, principal);
        //해당게시글 상세조회
        BoardDTO boardDTO = boardService.detail(id, true, request, principal);
        //댓글목록 조회
        List<CommentDTO> commentDTOS = commentService.list(id);
        boardDTO.setCommentCount(commentDTOS.size());
        //이전 페이지 Url 가져오는 코드
        String referer = request.getHeader("Referer");

        log.info(boardDTO);
        log.info(commentDTOS);

        /*Integer userId = boardService.userId(request, principal);

        model.addAttribute("userId", userId);*/
        model.addAttribute("loginUserId", loginUserId);
        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_NOTIFY.getDescription());
        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("Url", referer);

        return "board/notify/detail";
    }

    @GetMapping("/board_notify_reload")
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
        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_NOTIFY.getDescription());
        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "board/notify/detail";
    }



    @GetMapping("/board_notify_delete")
    public String deleteProc(Integer id,
                             Integer boardUserId,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {

        if(boardUserId != commentService.userId(request, principal) ) {
            return "redirect:/";
        }
        boardService.delete(id);

        return "redirect:/board_notify_list";
    }
}