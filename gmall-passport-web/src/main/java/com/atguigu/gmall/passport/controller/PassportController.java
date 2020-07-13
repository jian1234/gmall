package com.atguigu.gmall.passport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PassportController {

    @RequestMapping("index")
    public String index(){
        return  "index";
    }
}
