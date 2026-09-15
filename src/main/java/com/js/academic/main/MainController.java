package com.js.academic.main;

import com.js.academic.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    // 메인 페이지 (http://localhost:8080/main-view)
    @GetMapping("/main-view")
    public String mainView(HttpSession session, Model model) {
        return "main/main"; // templates/main/main.html 연결
    }
}