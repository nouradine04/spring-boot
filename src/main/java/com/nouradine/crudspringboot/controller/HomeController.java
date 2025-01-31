package com.nouradine.crudspringboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // Retourne une vue nommée "index.html"
    }
}

