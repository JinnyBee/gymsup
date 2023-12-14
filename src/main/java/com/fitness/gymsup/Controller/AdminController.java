/*
    파일명 : AdminController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Constant.BoardCategoryType;
import com.fitness.gymsup.Constant.UserRole;
import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import com.fitness.gymsup.Service.BoardService;
import com.fitness.gymsup.Service.CommentService;
import com.fitness.gymsup.Service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j

public class AdminController extends BoardBaseController {

    //S3 이미지 정보
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.region.static}")
    public String region;
    @Value("${imgUploadLocation}")
    public String folder;

    private final BasicUserService basicUserService;
    private final BoardService boardService;
    private final CommentService commentService;
    private final ReplyService replyService;

    //관리자페이지 - 관리자 정보 상세보기
    @GetMapping("/admin_detail")
    public String adminDetailForm(HttpServletRequest request,
                                  Principal principal,
                                  String errorMessage,
                                  UserDTO userDTO,
                                  String message,
                                  Model model) throws Exception {

        UserEntity userEntity = basicUserService.bringUserInfo(request, principal);

        model.addAttribute("message",message);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userEntity",userEntity);

        return "admin/admindetail";
    }

    //관리자페이지 - 전체 유저 정보
    @GetMapping("/admin_user_list")
    public String adminUserList(@PageableDefault(page = 1)Pageable pageable,
                                Model model) throws Exception {

        Page<UserDTO> userDTOS = basicUserService.userList(pageable);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(userDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, boardDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<userDTOS.getTotalPages()) ? startPage+blockLimit-1 : userDTOS.getTotalPages();

            prevPage = userDTOS.getNumber();
            currentPage = userDTOS.getNumber() + 1;
            nextPage = userDTOS.getNumber() + 2;
            lastPage = userDTOS.getTotalPages();
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("userDTOS",userDTOS);

        return "admin/userlist";
    }

    //관리자페이지 - 유저 정보 수정 폼
    @GetMapping("/admin_user_modify")
    public String adminUserModify(int id,
                                  UserDTO userDTO,
                                  String message,
                                  String errorMessage,
                                  Model model) throws Exception {

        UserDTO userDATA = basicUserService.findById(id);
        String userRole="";

        if (userDATA.getRole().equals(UserRole.USER)){
            userRole="유저";
        }else {
            userRole="관리자";
        }

        model.addAttribute("errorMessage",errorMessage);
        model.addAttribute("message",message);
        model.addAttribute("userRole",userRole);
        model.addAttribute("userDATA",userDATA);
        model.addAttribute("userDTO",userDTO);

        return "admin/usermodify";
    }

    //관리자페이지 - 유저 닉네임 중복체크
    @PostMapping("/admin_nickname_dup")
    public String adminNicknameDup(UserDTO userDTO,
                                   RedirectAttributes redirectAttributes) throws Exception{

        String message= basicUserService.dupNickname(userDTO);
        int id = userDTO.getId();

        redirectAttributes.addFlashAttribute("userDTO",userDTO);
        redirectAttributes.addAttribute("message", message);
        redirectAttributes.addAttribute("id", id);

        return "redirect:/admin_user_modify";
    }

    //관리자페이지 - 유저 닉네임 수정 처리
    @PostMapping("/admin_nickname_modify")
    public String adminNicknameModify(UserDTO userDTO,
                                      RedirectAttributes redirectAttributes) throws Exception {

        String errorMessage="유저닉네임이 변경되었습니다";
        int id = userDTO.getId();
        basicUserService.adminUpdateNickname(userDTO);

        redirectAttributes.addAttribute("errorMessage",errorMessage);
        redirectAttributes.addAttribute("id",id);
        return "redirect:/admin_user_modify";
    }

    //관리자페이지 - 유저 등급 수정 처리
    @PostMapping("/admin_role_modify")
    public String adminRoleModify(UserDTO userDTO,
                                  RedirectAttributes redirectAttributes) throws Exception {

        String errorMessage="";
        int id = userDTO.getId();

        if(userDTO.getRole().equals(UserRole.USER)){
            errorMessage="유저등급이 유저로 변경되었습니다.";
        }else{
            errorMessage="유저등급이 관리자로 변경되었습니다.";
        }
        basicUserService.adminUpdateRole(userDTO);

        redirectAttributes.addAttribute("errorMessage",errorMessage);
        redirectAttributes.addAttribute("id",id);

        return "redirect:/admin_user_modify";
    }

    //관리자페이지 - 유저 정지여부 수정 처리
    @PostMapping("/admin_ban_modify")
    public String adminBanModify(UserDTO userDTO,
                                 RedirectAttributes redirectAttributes) throws Exception {

        String errorMessage="";
        int id = userDTO.getId();

        if(userDTO.isBan()==true){
            errorMessage="유저가 정지 되었습니다.";
        }else{
            errorMessage="유저 정지가 해지 되었습니다";
        }

        basicUserService.adminUpdateBan(userDTO);
        redirectAttributes.addAttribute("errorMessage",errorMessage);
        redirectAttributes.addAttribute("id",id);

        return "redirect:/admin_user_modify";
    }

    //전체 게시판 - 게시글 삭제
    @GetMapping("/admin_board_delete")
    public String boardDeleteProc(Integer id,
                                  Integer page,
                                  RedirectAttributes redirectAttributes)throws Exception{

        boardService.delete(id);
        redirectAttributes.addAttribute("page",page);

        return "redirect:/board_list";
    }

    //자유 게시판 - 게시글 삭제
    @GetMapping("/admin_free_delete")
    public String freeDeleteProc(Integer id,
                                 Integer page,
                                 RedirectAttributes redirectAttributes) throws Exception{

        boardService.delete(id);
        redirectAttributes.addAttribute("page",page);

        return "redirect:/board_free_list";
    }

    //운동 팁 게시판 - 게시글 삭제
    @GetMapping("/admin_tip_delete")
    public String tipDeleteProc(Integer id,
                                Integer page,
                                RedirectAttributes redirectAttributes)throws Exception{

        boardService.delete(id);
        redirectAttributes.addAttribute("page",page);

        return "redirect:/board_tip_list";
    }

    //운동+식단 일기 게시판 - 게시글 삭제
    @GetMapping("/admin_diary_delete")
    public String diaryDeleteProc(Integer id,
                                  Integer page,
                                  RedirectAttributes redirectAttributes)throws Exception{
        boardService.delete(id);
        redirectAttributes.addAttribute("page",page);

        return "redirect:/board_diary_list";
    }

    //고민나눔 게시판 - 게시글 삭제
    @GetMapping("/admin_qna_delete")
    public String qnaDeleteProc(Integer id,
                                Integer page,
                                RedirectAttributes redirectAttributes)throws Exception{

        boardService.delete(id);
        redirectAttributes.addAttribute("page",page);

        return "redirect:/board_qna_list";
    }

    //댓글 삭제
    @GetMapping("/admin_comment_delete")
    public String commentDeleteProc(Integer bid,
                                    Integer id,
                                    String categoryType,
                                    RedirectAttributes redirectAttributes)throws Exception{
        commentService.delete(id);
        redirectAttributes.addAttribute("id",bid);

        return "redirect:"+getReloadRedirectUrl(categoryType);
    }

    //답글 삭제
    @GetMapping("/admin_reply_delete")
    public String adminReplyDeleteProc(int bid,
                                       int id,
                                       String categoryType,
                                       RedirectAttributes redirectAttributes)throws Exception{

        redirectAttributes.addAttribute("id",bid);

        return "redirect:" +getReloadRedirectUrl(categoryType);
    }


    //관리자 공지사항 목록
    @GetMapping("/admin_notify_list")
    public String adminNotifyList(@PageableDefault(page = 1) Pageable pageable,
                                  @RequestParam(value = "type", defaultValue = "") String type,
                                  @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                  Model model)throws Exception{
        Page<BoardDTO> boardDTOS = boardService.list(pageable, BoardCategoryType.BTYPE_NOTIFY, type, keyword);

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

        model.addAttribute("categoryTypeDesc", BoardCategoryType.BTYPE_NOTIFY.getDescription());
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardDTOS", boardDTOS);



        return "admin/notify_list";
    }

    //관리자 페이지 - 공지사항 수정 폼
    @GetMapping("/admin_notify_modify")
    public String modifyForm(Integer id,
                             HttpServletRequest request,
                             Principal principal,
                             Model model) throws Exception {


        BoardDTO boardDTO = boardService.detail(id, false, request, principal);

        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);

        return "board/notify/modify";
    }

    //관리자 페이지 - 공지사항 수정
    @PostMapping("/admin_notify_modify")
    public String modifyProc(@Valid BoardDTO boardDTO,
                             BindingResult bindingResult,
                             List<MultipartFile> imgFiles,
                             Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            return "board/notify/modify";
        }

        boardService.modify(boardDTO, imgFiles);

        return "redirect:/admin_notify_list";
    }

    //관리자페이지 - 공지사항 게시글 삭제
    @GetMapping("/admin_notify_delete")
    public String notifyDeleteProc(Integer id)throws Exception{
        boardService.delete(id);

        return "redirect:/admin_notify_list";
    }

    //관리자페이지 - 공지사항 등록 폼
    @GetMapping("/admin_notify_register")
    public String registerForm(Model model) throws Exception {

        BoardDTO boardDTO = new BoardDTO();

        boardDTO.setCategoryType(BoardCategoryType.BTYPE_NOTIFY);
        log.info(boardDTO.getCategoryType().name());
        log.info(boardDTO.getCategoryType().getDescription());

        model.addAttribute("boardDTO", boardDTO);

        return "board/notify/register";
    }

    //관리자페이지 - 공지사항 등록
    @PostMapping("/admin_notify_register")
    public String registerProc(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               List<MultipartFile> imgFiles,
                               Principal principal,
                               HttpServletRequest request,
                               Model model) throws Exception {

        log.info(boardDTO.getCategoryType().name());
        if (bindingResult.hasErrors()) {
            return "board/tip/register";
        }
        boardService.register(boardDTO, imgFiles, request, principal);

        return "redirect:/admin_notify_list";
    }
}
