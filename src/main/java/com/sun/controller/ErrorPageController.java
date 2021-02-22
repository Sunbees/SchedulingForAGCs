package com.sun.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.ApiOperation;


@Controller
public class ErrorPageController {
    @ApiOperation(value = "跳转到index页面", notes = "跳转到index页面")
    @RequestMapping("/schedule/**")
    public ModelAndView getNeMoInTree() {

        System.out.println("=========跳转到index页面===================");
        return new ModelAndView("index");
    }

}
