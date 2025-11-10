package com.malugu.springboot_starter_project.uaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaygroundController {

    @GetMapping("/sandbox")
    public String playground() {
        return "forward:/sandbox.html";
    }
}
