package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j

public class AdminController {

    private final BasicUserService basicUserService;

    @GetMapping("/admin_detail")
    public String adminDetailForm(HttpServletRequest request, Model model, Principal principal,
                                  String errorMessage, UserDTO userDTO, String message)throws Exception{
        UserEntity userEntity = basicUserService.bringUserInfo(request, principal);

        model.addAttribute("message",message);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("userEntity",userEntity);
        return "admin/admindetail";
    }

    @GetMapping("/admin_user_list")
    public String adminUserList()throws Exception{
        return "admin/userlist";
    }
}
