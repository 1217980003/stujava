package com.stu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class TestController {

    @RequestMapping("/index")
    public String index(){
        return "demo";
    }
}