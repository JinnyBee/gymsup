package com.fitness.gymsup.Controller;

import com.fitness.gymsup.Entity.UserEntity;
import com.fitness.gymsup.Service.BasicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final BasicUserService basicUserService;

    @GetMapping({"/", "/index"})
    public String main(Model model, HttpServletRequest request, Principal principal) throws Exception {
        if(request !=null && principal!=null){
            UserEntity userEntity = basicUserService.bringUserInfo(request, principal);
            model.addAttribute("userEntity",userEntity);
        }
        return "index";
    }
    @GetMapping({"/test"})
    public String test() throws Exception {
        return "test";
    }
}