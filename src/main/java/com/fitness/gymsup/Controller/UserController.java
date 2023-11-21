package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

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
    public String joinForm(Model model) throws Exception {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO",userDTO);
        return "user/join";
    }
    @PostMapping("/user_join")
    public String joinProc(@Valid UserDTO userDTO, BindingResult bindingResult,
                           Model model) throws Exception {
        if (bindingResult.hasErrors()){
            return "user/join";
        }
        try {
            basicUserService.saveMember(userDTO);
            model.addAttribute("errorMessage", "가입에 성공하였습니다");
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/register";
        }
        return "redirect:/";
    }
    @GetMapping("/user_login")
    public String loginForm() throws Exception {

        return "user/login";
    }
    @GetMapping("/user_detail")
    public String detailForm(HttpServletRequest request, Model model, Principal principal) throws Exception {
        UserEntity userEntity = basicUserService.bringUserInfo(request, principal);

        model.addAttribute("userEntity",userEntity);
        return "user/detail";
    }
    @GetMapping("/user_modify")
    public String modifyForm(Model model) throws Exception {
        return "user/modify";
    }
    @PostMapping("/user_modify")
    public String modifyProc(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_cancel")
    public String cancelProc(Model model) throws Exception {
        return "redirect:/";
    }
    @GetMapping("/user_mywrite")
    public String myWrite(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_mycomment")
    public String myComment(Model model) throws Exception {
        return "redirect:/user/detail";
    }
    @GetMapping("/user_mybookmark")
    public String myBookmark(Model model) throws Exception {
        return "redirect:/user/detail";
    }
}