package com.kakaopay.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class WebController {

    @GetMapping("/")
    public String index () {
        return "index";
    }

    @RequestMapping("/api-docs")
    public void apiDocs(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("/swagger-ui.html");
    }
}
