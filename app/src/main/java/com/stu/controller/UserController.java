package com.stu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.stu.pojo.User;
import com.stu.service.IUserService;
import com.stu.testmybatis.TestMybatis;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;
    
    private static Logger logger = Logger.getLogger(UserController.class);
    
    @RequestMapping("/showUser")
    public String toIndex(HttpServletRequest request, Model model) {
        int userId = Integer.parseInt(request.getParameter("id"));
        User user = this.userService.findUserById(userId);
        model.addAttribute("user", user);
        logger.info(">>>info:"+JSON.toJSONString(user));
        return "User";
    }
}