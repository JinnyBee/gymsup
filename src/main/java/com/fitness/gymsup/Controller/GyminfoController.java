/*
    파일명 : GyminfoController.java
    기 능 :
    작성일 : 2023.12.08
    작성자 :
*/
package com.fitness.gymsup.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GyminfoController {
    @GetMapping("/gyminfo_about")
    public String aboutForm(Model model) throws Exception {
        return "gyminfo/about";
    }
}