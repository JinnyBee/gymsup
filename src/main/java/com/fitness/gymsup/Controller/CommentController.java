package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.ReplyDTO;
import com.fitness.gymsup.Entity.CommentEntity;
import com.fitness.gymsup.Service.CommentService;
import com.fitness.gymsup.Service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Parameter;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CommentController extends BaseController {
    private final CommentService commentService;
    private final ReplyService replyService;

    @PostMapping("/comment_register")
    public String registerCommentProc(CommentDTO commentDTO,
                                      String categoryType,
                                      HttpServletRequest request,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) throws Exception {
        log.info("boardId : " + commentDTO.getBoardId() + ", userId : " + commentDTO.getUserId() + ", categoryType : " + categoryType);

        commentService.register(commentDTO, request, principal);
        redirectAttributes.addAttribute("id", commentDTO.getBoardId());
        log.info("#### " + commentDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }

    //boardId: 조회할 게시글 id
/*    @PostMapping("/comment_register/{boardId}")
    //no 게시글을 조회할 번호, commentDTO 댓글 정보
    public ResponseEntity<String> registerCommentProc(@PathVariable Integer boardId,
                                                      @RequestBody CommentDTO commentDTO,
                                                      HttpServletRequest request,
                                                      Principal principal) throws  Exception {
        ResponseEntity<String> entity = null;

        try {
            *//*
            //섹션이 있는 경우 사용
            String userId = (String) session.getAttribute("userId"); //로드인시 섹션에 저장한 Id
            commentDTO.setNickname(userId); //섹션에 Id를 댓글에 저장 *//*

            log.info(commentDTO);
            commentDTO.setBoardId(boardId);
            log.info(commentDTO.getBoardId());
            commentService.register(commentDTO, request, principal); //댓글 등록
            entity = new ResponseEntity<String>("success", HttpStatus.OK); //성공메세지와 상태 저장
        } catch(Exception e) { //저장을 실패했을 때
            entity = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); //오류메세지와 상태 저장
        }
        return entity; //요청한 페이지로 되돌아간다.
    }*/

    @PostMapping("/comment_modify")
    public String modifyCommentProc(CommentDTO commentDTO,
                                    String categoryType,
                                    HttpServletRequest request,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) throws Exception{
        log.info(commentDTO);
        log.info(categoryType);

        commentService.modify(commentDTO, request, principal);
        redirectAttributes.addAttribute("id", commentDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }

    @GetMapping("/comment_remove")
    public String removeCommentProc(int bid,
                                    int id,
                                    String categoryType,
                                    RedirectAttributes redirectAttributes) throws Exception {
        commentService.remove(id);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:" + getRedirectUrl(categoryType);
    }

    @GetMapping("/comment_user_all")
    public String commentUserAll(HttpServletRequest request, Principal principal)throws Exception{
        commentService.userCommentRemove(request, principal);
        return "redirect:/";
    }


    @PostMapping("/reply_register")
    public String registerReplyProc(@Valid ReplyDTO replyDTO,
                                    BindingResult bindingResult,
                                    String categoryType,
                                    HttpServletRequest request,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) throws Exception {
        log.info("commentId : " + replyDTO.getCommentId() + ", userId : " + replyDTO.getUserId() + "categoryType : " + categoryType);
        if (bindingResult.hasErrors()) {
            log.info(getRedirectUrl(categoryType));
            return "redirect:" + getRedirectUrl(categoryType);
        }

        replyService.register(replyDTO, request, principal);
        redirectAttributes.addAttribute("id", replyDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }
    @PostMapping("/reply_modify")
    public String modifyReplyProc(ReplyDTO replyDTO,
                                    HttpServletRequest request,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes,
                                    String categoryType) throws Exception{
        replyService.modify(replyDTO, request, principal);
        redirectAttributes.addAttribute("id", replyDTO.getBoardId());

        return "redirect:" + getRedirectUrl(categoryType);
    }

    @GetMapping("/reply_remove")
    public String removeReplyProc(int bid,
                                  int id,
                                  String categoryType,
                                  RedirectAttributes redirectAttributes) throws Exception {
        replyService.remove(id);
        redirectAttributes.addAttribute("id", bid);

        return "redirect:" + getRedirectUrl(categoryType);
    }
}
