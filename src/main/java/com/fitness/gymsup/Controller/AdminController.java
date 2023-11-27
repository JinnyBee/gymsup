package com.fitness.gymsup.Controller;

import com.fitness.gymsup.DTO.UserDTO;
import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public String adminUserList(@PageableDefault(page = 1)Pageable pageable,
                                Model model)throws Exception{
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
}
