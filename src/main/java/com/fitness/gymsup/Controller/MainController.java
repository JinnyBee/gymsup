package com.fitness.gymsup.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping({"/", "/index"})
    public String main() throws Exception {
        return "index";
    }
    @GetMapping({"/test"})
    public String test() throws Exception {
        return "test";
    }
}