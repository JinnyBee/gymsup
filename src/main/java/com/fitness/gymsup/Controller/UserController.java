package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.BoardDTO;
import com.fitness.gymsup.DTO.CommentDTO;
import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j

public class UserController {

    private final BasicUserService basicUserService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/user_list")
    public String listForm(Model model) throws Exception {
        return "user/list";
    }
    @GetMapping("/user_join")
    public String joinForm(Model model , String message, String emessage, HttpServletRequest request) throws Exception {
            Map<String, ?>flashMap = RequestContextUtils.getInputFlashMap(request);
            UserDTO userDTO = new UserDTO();
            if (flashMap != null){
                userDTO = (UserDTO) flashMap.get("userDTO");
            }

            model.addAttribute("userDTO", userDTO);
            model.addAttribute("emessage",emessage);
            model.addAttribute("message",message);


        return "user/join";
    }
    @PostMapping("/user_join")
    public String joinProc(@Valid UserDTO userDTO, BindingResult bindingResult,
                           Model model,RedirectAttributes redirectAttributes) throws Exception {
        if (bindingResult.hasErrors()){
            return "user/join";
        }

        String email = basicUserService.dupEmail(userDTO);
        String nickname = basicUserService.dupNickname(userDTO);

        if(email.equals("이메일 사용이 가능합니다.")&& nickname.equals("닉네임 사용이 가능합니다.")){
            try {
                basicUserService.saveMember(userDTO);
                redirectAttributes.addAttribute("errorMessage", "가입에 성공하였습니다");
            }catch (IllegalStateException e){
                redirectAttributes.addAttribute("errorMessage",e.getMessage());
                return "member/register";
            }
            return "redirect:/user_login";
        }else {
            model.addAttribute("errorMessage","아이디나 닉네임이 중복입니다.");
            return "user/join";
        }


    }

    @GetMapping("/user_login")
    public String loginForm(String errorMessage, Model model) throws Exception {
        model.addAttribute("errorMessage",errorMessage);
        return "user/login";
    }

    @GetMapping("/user_login_error")
    public String loginError(Model model)throws Exception {
        model.addAttribute("errorMessage", "아이디 또는 비밀번호를 확인해 주세요.");
        return "user/login";
    }

    @GetMapping("/user_detail")
    public String detailForm(HttpServletRequest request, Model model, Principal principal,
                             String errorMessage, UserDTO userDTO, String message) throws Exception {
        UserEntity userEntity = basicUserService.bringUserInfo(request, principal);

        model.addAttribute("message",message);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userEntity",userEntity);
        return "user/detail";
    }


    @PostMapping("/user_nickname_update")
    public String nicknameUpdate(UserDTO userDTO, Principal principal, HttpServletRequest request)throws Exception{
        basicUserService.updateNickname(userDTO, principal, request);
        return "redirect:/user_detail";
    }

    @PostMapping("/user_nickname_dup")
    public String nicknameDupt(UserDTO userDTO, RedirectAttributes redirectAttributes)throws Exception{
        String message = basicUserService.dupNickname(userDTO);
        redirectAttributes.addAttribute("message", message);
        redirectAttributes.addFlashAttribute("userDTO", userDTO);

        return "redirect:/user_detail";
    }

    @GetMapping("/user_cancel")
    public String cancelForm(Model model) throws Exception {
        return "user/cancel";
    }

    @GetMapping ("/user_cancel_proc")
    public String cancelProc(Model model,Principal principal, HttpServletRequest request) throws Exception {
        basicUserService.cancelUser(principal, request);
        return "redirect:/user_logout";
    }

    @GetMapping("/user_mywrite")
    public String myWrite(@PageableDefault(page = 1) Pageable pageable
            , Model model, HttpServletRequest request, Principal principal) throws Exception {
        Page<BoardDTO> boardDTOS = basicUserService.myWrite(pageable, request, principal);
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

        model.addAttribute("boardDTOS", boardDTOS);


        return "user/mywrite";
    }

    @GetMapping("/user_mycomment")
    public String myComment(@PageableDefault(page = 1) Pageable pageable
            , Model model, HttpServletRequest request, Principal principal) throws Exception {
        Page<CommentDTO> commentDTOS = basicUserService.myComment(pageable, request, principal);

        int blockLimit = 5;
        int startPage, endPage, prevPage, currentPage, nextPage, lastPage;

        if(commentDTOS.isEmpty()) {
            startPage = 0;
            endPage = 0;
            prevPage = 0;
            currentPage = 0;
            nextPage = 0;
            lastPage = 0;
        } else {
            startPage = (((int)(Math.ceil((double) pageable.getPageNumber()/blockLimit)))-1) * blockLimit + 1;
            //endPage = Math.min(startPage+blockLimit-1, commentDTOS.getTotalPages());
            endPage = ((startPage+blockLimit-1)<commentDTOS.getTotalPages()) ? startPage+blockLimit-1 : commentDTOS.getTotalPages();

            prevPage = commentDTOS.getNumber();
            currentPage = commentDTOS.getNumber() + 1;
            nextPage = commentDTOS.getNumber() + 2;
            lastPage = commentDTOS.getTotalPages();
        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("commentDTOS", commentDTOS);
        
        return "user/mycomment";
    }
    @PostMapping("/user_regDupNickname")
    public String regDupNickname(UserDTO userDTO, RedirectAttributes redirectAttributes)throws Exception{
        String message = basicUserService.dupNickname(userDTO);
        redirectAttributes.addAttribute("message", message);
        redirectAttributes.addFlashAttribute("userDTO", userDTO);

        return "redirect:/user_join";
    }

    @PostMapping("/user_regDupEmail")
    public String regDupEmail(UserDTO userDTO,RedirectAttributes redirectAttributes)throws Exception{
        String eMessage = basicUserService.dupEmail(userDTO);
        redirectAttributes.addAttribute("emessage", eMessage);
        redirectAttributes.addFlashAttribute("userDTO",userDTO);
        return "redirect:/user_join";
    }

    @GetMapping("/user_password_confirm")
    public String passwordConfirmForm(String errorMessage, Model model)throws Exception{
        model.addAttribute("errorMessage",errorMessage);
        return "user/passwordform";
    }

    @PostMapping("/user_password_confirm")
    public String passwordConfirmProc(Principal principal, String apassword, Model model,RedirectAttributes redirectAttributes)throws Exception{

        String errorMessage = "";
        String bpassword = basicUserService.bringPassword(principal);

        boolean result = passwordEncoder.matches(apassword, bpassword);
        if(result){
            errorMessage="현재 비밀번호와 일치합니다.";
            model.addAttribute("errorMessage",errorMessage);
            return "user/passwordproc";
        }else {
            errorMessage="현재 비밀번호와 다릅니다.";
            redirectAttributes.addAttribute("errorMessage",errorMessage);
            return "redirect:/user_password_confirm";
        }

    }


    @PostMapping("/user_password_update")
    public String passwordUpdateProc(@Valid UserDTO userDTO,BindingResult bindingResult,
                                     Principal principal, Model model,
                                     RedirectAttributes redirectAttributes)throws Exception{
        if (bindingResult.hasErrors()){
            return "user/passwordproc";
        }

        model.addAttribute("userDTO", userDTO);
        redirectAttributes.addAttribute("errorMessage","비밀번호 변경에 성공했습니다.");
        basicUserService.updatePassword(userDTO, principal);
        return "redirect:/user_detail";
    }

}